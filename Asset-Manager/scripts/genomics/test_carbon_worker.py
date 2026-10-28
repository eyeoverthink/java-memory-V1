"""Protocol and codec regressions; no model download or inference is required."""
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest

spec = importlib.util.spec_from_file_location('carbon_worker', Path(__file__).with_name('carbon_worker.py'))
worker = importlib.util.module_from_spec(spec)
spec.loader.exec_module(worker)


class CarbonCodecTests(unittest.TestCase):
    def setUp(self):
        self.codec = worker.DnaCodec({'k': 6, 'dna_start_id': 151669})

    def test_published_vocabulary_ids(self):
        # CCCCCC=154402 is also documented in the upstream tokenizer source.
        self.assertEqual(self.codec.encode('AAAAAATTTTTTCCCCCCGGGGGG', True)[0],
                         [151669, 151672, 153037, 154402, 155767, 151670])

    def test_all_six_mers_keep_their_positions(self):
        sequence = ''.join(self.codec.kmers)
        ids, aligned = self.codec.encode(sequence)
        self.assertEqual(len(set(ids[1:])), 4096)
        self.assertEqual(self.codec.decode(ids), sequence)
        self.assertEqual(aligned, 6 * 4096)

    def test_partial_tail_is_explicitly_excluded(self):
        ids, used = self.codec.encode('ACGTACGT')
        self.assertEqual(used, 6)
        self.assertEqual(self.codec.decode(ids), 'ACGTAC')

    def test_ambiguity_and_short_input_are_not_silently_rewritten(self):
        for sequence in ['acgtac', 'ACGTNC', '', 'ACG', '<dna>ACGTAC']:
            with self.subTest(sequence=sequence), self.assertRaises(ValueError):
                self.codec.encode(sequence)

    def test_multirecord_fasta_and_bad_format(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / 'sample.fasta'
            path.write_text('>first\nacg tac\n; comment\n>second\nTTTTTT\n')
            self.assertEqual(list(worker.fasta_records(path)), [('first', 'ACGTAC'), ('second', 'TTTTTT')])
            path.write_text('ACGTAC\n')
            with self.assertRaises(ValueError):
                list(worker.fasta_records(path))

    def test_existing_pain_preview_matches_known_generator(self):
        self.assertTrue(worker.label_encoding('PAIN').startswith(
            'TACGTGTGCGCGGACCTCTGCAGATTAATTCACAATAGTCTGTGACGCATGAGACCTACGGATTACGTTTTATCGTATCC'))

    def test_reports_preserve_prior_artifacts(self):
        with tempfile.TemporaryDirectory() as directory:
            first = worker.save_report(Path(directory), 'fixture', {'value': 1})
            second = worker.save_report(Path(directory), 'fixture', {'value': 2})
            self.assertNotEqual(first, second)
            self.assertEqual(json.loads(first.read_text())['value'], 1)
            self.assertEqual(json.loads(second.read_text())['value'], 2)


if __name__ == '__main__':
    unittest.main()
