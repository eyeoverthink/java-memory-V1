import contextlib
import hashlib
import io
import json
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest

import enigma_worker
from needle_worker import validate

HERE = Path(__file__).resolve().parent
ROOT = next(parent / 'integrations/fraymus-system-new' for parent in HERE.parents
            if (parent / 'integrations/fraymus-system-new/source-intake.json').is_file())
NODE = shutil.which('node')


class SpecialistWorkerTests(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(prefix='fraynix-specialist-test-')
        self.root = Path(self.temporary.name)

    def tearDown(self):
        self.temporary.cleanup()

    def transform(self, request):
        return enigma_worker.transform(request, ROOT, self.root / 'artifacts', NODE)

    def native(self, request):
        source = self.root / 'request.json'
        source.write_text(json.dumps(request), encoding='utf-8')
        process = subprocess.run([NODE, str(HERE / 'native_brain_worker.mjs'), str(ROOT), str(source),
                                  str(self.root / 'mind.json'), str(self.root / 'artifacts')],
                                 capture_output=True, text=True, timeout=30)
        result = json.loads(process.stdout)
        self.assertEqual(process.returncode, 0, result)
        self.assertTrue(result['ok'])
        return result['result']

    def test_all_supplied_source_files_preserved(self):
        manifest = json.loads((ROOT / 'source-intake.json').read_text())
        self.assertEqual(len(manifest['files']), 210)
        for entry in manifest['files']:
            relative = Path(*Path(entry['integrated']).parts[2:])
            self.assertEqual(hashlib.sha256((ROOT / relative).read_bytes()).hexdigest(), entry['sha256'])

    def test_exact_byte_roundtrips_png_and_wasm(self):
        for payload in (b'', bytes(range(256)), b'Fraymus state\x00' * 1000):
            original = self.root / 'payload.bin'
            original.write_bytes(payload)
            for png in (False, True):
                encoded = self.transform({'operation': 'encode', 'path': str(original), 'rotors': ['owner', 'test'], 'png': png})
                decoded = self.transform({'operation': 'decode', 'path': encoded['output'], 'rotors': ['owner', 'test']})
                self.assertEqual(Path(decoded['output']).read_bytes(), payload)

    def test_wrong_rotors_and_corrupt_png_do_not_write_recovered_data(self):
        original = self.root / 'payload.bin'
        original.write_bytes(bytes(range(256)))
        encoded = self.transform({'operation': 'encode', 'path': str(original), 'rotors': ['owner'], 'png': True})
        before = set((self.root / 'artifacts').iterdir())
        with self.assertRaises(ValueError):
            self.transform({'operation': 'decode', 'path': encoded['output'], 'rotors': ['other']})
        corrupt = bytearray(Path(encoded['output']).read_bytes())
        corrupt[20] ^= 1
        damaged = self.root / 'damaged.png'
        damaged.write_bytes(corrupt)
        with self.assertRaisesRegex(ValueError, 'CRC'):
            self.transform({'operation': 'decode', 'path': str(damaged), 'rotors': ['owner']})
        self.assertEqual(set((self.root / 'artifacts').iterdir()), before)

    def test_legacy_requires_independent_hash_and_preserves_bytes(self):
        module = enigma_worker.load_sources(ROOT)
        source = self.root / 'source.bin'
        payload = bytes(range(256))
        source.write_bytes(payload)
        with contextlib.redirect_stdout(io.StringIO()):
            legacy = module.enc_file(str(source), ['owner'], str(self.root / 'legacy.wasm'), png_out=False)
        with self.assertRaisesRegex(ValueError, 'independent'):
            self.transform({'operation': 'decode', 'path': legacy, 'rotors': ['owner']})
        result = self.transform({'operation': 'decode', 'path': legacy, 'rotors': ['owner'],
                                 'expected_sha256': hashlib.sha256(payload).hexdigest()})
        self.assertEqual(Path(result['output']).read_bytes(), payload)

    def test_tampered_wasm_and_payload_rejected(self):
        source = self.root / 'source.bin'
        source.write_bytes(b'actual state')
        encoded = self.transform({'operation': 'encode', 'path': str(source), 'rotors': ['owner']})
        data = Path(encoded['output']).read_bytes()
        for corrupted in (data[:-1], data[:4] + b'\x02' + data[5:], data[:-1] + bytes([data[-1] ^ 1])):
            path = self.root / 'corrupt.wasm'
            path.write_bytes(corrupted)
            with self.assertRaises((ValueError, AssertionError, IndexError)):
                self.transform({'operation': 'decode', 'path': str(path), 'rotors': ['owner']})

    def test_native_learning_is_reused_and_inherited_facts_rejected(self):
        first = self.native({'operation': 'think', 'goal': '14 - 9'})
        self.assertEqual(first['got'], 5)
        self.assertTrue(any(step['phase'] == 'rewrite' for step in first['steps']))
        second = self.native({'operation': 'think', 'goal': '100 - 37'})
        self.assertEqual(second['got'], 63)
        self.assertFalse(any(step['phase'] == 'rewrite' for step in second['steps']))
        state = (self.root / 'mind.json').read_bytes()
        self.assertEqual(self.native({'operation': 'think', 'goal': 'recall constructor'})['verdict'], 'FAIL')
        self.assertEqual((self.root / 'mind.json').read_bytes(), state)

    def test_native_vm_and_real_wasm_agree(self):
        result = self.native({'operation': 'vm', 'source': 'PUSH_IMM 6\nPUSH_IMM 7\nMUL\nHALT'})
        self.assertEqual(result['wasm_result'], 42)
        self.assertTrue(result['interpreter_wasm_agree'])
        self.assertEqual(hashlib.sha256(Path(result['output']).read_bytes()).hexdigest(), result['output_sha256'])

    def test_native_byte_codec_and_method_allowlist(self):
        packed = self.native({'operation': 'method', 'method': 'bytesToDna', 'args': [list(range(256))]})['value']
        decoded = self.native({'operation': 'method', 'method': 'dnaToBytes', 'args': [packed]})['value']
        self.assertEqual(decoded, list(range(256)))
        self.assertIn('ledgerWithOutliers', self.native({'operation': 'methods'})['methods'])

    def test_needle_schema_rejects_wrong_types_unknown_fields_and_nonfinite(self):
        schema = {'type': 'object', 'properties': {'amount': {'type': 'integer', 'minimum': 0, 'maximum': 5}}, 'required': ['amount']}
        validate({'amount': 2}, schema)
        for value in ({'amount': True}, {'amount': 6}, {'amount': 1, 'execute': 'shell'}, {}, {'amount': float('nan')}):
            with self.assertRaises(ValueError):
                validate(value, schema)


if __name__ == '__main__':
    unittest.main()
