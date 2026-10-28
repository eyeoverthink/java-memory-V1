"""Owner-code Enigma/Plasmid bridge with strict, versioned lossless verification.

This is a reversible container with corruption checks, not authenticated encryption.
Legacy Enigma v1 decoding additionally requires an independently supplied payload SHA.
"""
import contextlib
import hashlib
import importlib.util
import json
import math
from pathlib import Path
import struct
import subprocess
import sys
import uuid
import zlib


def digest(data):
    return hashlib.sha256(data).hexdigest()


def load_sources(root):
    sys.path.insert(0, str(root / 'plasmid'))
    spec = importlib.util.spec_from_file_location('owner_enigma', root / 'enigma/enigma.py')
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


def strict_png(blob):
    if not blob.startswith(b'\x89PNG\r\n\x1a\n'):
        raise ValueError('Invalid PNG signature')
    offset, ended = 8, False
    while offset < len(blob):
        if offset + 12 > len(blob):
            raise ValueError('Truncated PNG chunk')
        size = int.from_bytes(blob[offset:offset + 4], 'big')
        end = offset + 12 + size
        if end > len(blob):
            raise ValueError('PNG chunk extends past file')
        tag_data = blob[offset + 4:offset + 8 + size]
        crc = int.from_bytes(blob[offset + 8 + size:end], 'big')
        if zlib.crc32(tag_data) & 0xffffffff != crc:
            raise ValueError('PNG CRC mismatch')
        tag = blob[offset + 4:offset + 8]
        offset = end
        if tag == b'IEND':
            ended = True
            break
    if not ended or offset != len(blob):
        raise ValueError('PNG terminator or trailing data is invalid')


def strict_container(module, wasm, node):
    if wasm[:8] != b'\0asm\x01\0\0\0':
        raise ValueError('Invalid WASM version or signature')
    offset = 8
    while offset < len(wasm):
        offset += 1
        size, offset = module.wa.read_uleb(wasm, offset)
        if offset + size > len(wasm):
            raise ValueError('WASM section extends past file')
        offset += size
    if offset != len(wasm):
        raise ValueError('Truncated WASM')
    # Static validation only. The module and recovered payload are never executed.
    validation = subprocess.run([node, '-e', "let a=[];process.stdin.on('data',b=>a.push(b));process.stdin.on('end',()=>process.exit(WebAssembly.validate(Buffer.concat(a))?0:1));"],
                                input=wasm, capture_output=True, timeout=30)
    if validation.returncode:
        raise ValueError('WASM static validation failed')
    data = module.wa.read_container(wasm)
    start, size, manifest_size = data['payload_off'], data['payload_len'], data['manifest_len']
    if start < 24 + manifest_size or start + size != len(data['blob']):
        raise ValueError('Invalid WASM payload/manifest bounds')
    manifest = json.loads(data['blob'][24:24 + manifest_size])
    body = data['blob'][start:start + size]
    return data, manifest, body


def transform(request, root, output_dir, node):
    module = load_sources(root)
    rotors = request['rotors']
    if not isinstance(rotors, list) or not rotors or any(not isinstance(r, str) or not r for r in rotors):
        raise ValueError('An explicit nonempty list of rotor identifiers is required')
    source = Path(request['path']).resolve()
    original = source.read_bytes()
    key = module.rotor_material(rotors)
    catalog = int(hashlib.sha256(key + b'|L3').hexdigest()[:6], 16)
    table = module.warp.build_dict(hashlib.sha256(key + b'|L2').digest())
    output_dir.mkdir(parents=True, exist_ok=True)
    operation = request['operation']
    if operation == 'encode':
        transformed = module.xor_stream(original, hashlib.sha256(key + b'|L1').digest())
        warped, _ = module.warp.warp(transformed, table)
        body = module.dna_anchors(module.dna.pack(warped), catalog).encode('ascii')
        recovered = module.xor_stream(module.warp.dewarp(module.dna.unpack(module.dna_deanchors(body.decode('ascii'), catalog)), table),
                                      hashlib.sha256(key + b'|L1').digest())
        if recovered != original:
            raise ValueError('Enigma encode roundtrip differs from input')
        manifest = {'format': 'fraynix-enigma-v2', 'payload_sha256': digest(original),
                    'payload_bytes': len(original), 'body_sha256': digest(body), 'rotor_id': digest(key),
                    'security_scope': 'reversible representation and unkeyed integrity; not authenticated encryption'}
        metadata = json.dumps(manifest, sort_keys=True).encode()
        pages = max(1, math.ceil((24 + len(metadata) + 3 + len(body)) / 65536))
        wasm = module.wa.build_module(metadata, body, module.wa.fnv1a(body), pages)
        strict_container(module, wasm, node)
        extension = '.wasm'
        if request.get('png', False):
            side = 256
            while module.png.capacity_bytes(side, side) < len(wasm) + 64:
                side *= 2
            rgb = module.png.embed(module.png.noise_fill(side, side), wasm, side, side)
            output = module.png.write_png(side, side, rgb)
            strict_png(output)
            width, height, decoded_rgb = module.png.read_png(output)
            if module.png.extract(decoded_rgb, width, height) != wasm:
                raise ValueError('PNG extraction differs from the encoded container')
            extension = '.png'
        else:
            output = wasm
        target = output_dir / ('enigma-' + uuid.uuid4().hex + extension)
    elif operation == 'decode':
        wasm = original
        if original.startswith(b'\x89PNG'):
            strict_png(original)
            width, height, rgb = module.png.read_png(original)
            wasm = module.png.extract(rgb, width, height)
        container, manifest, body = strict_container(module, wasm, node)
        version2 = manifest.get('format') == 'fraynix-enigma-v2'
        if version2:
            if manifest.get('rotor_id') != digest(key) or manifest.get('body_sha256') != digest(body):
                raise ValueError('Rotor or container payload hash mismatch')
            if container['tag'] != module.wa.fnv1a(body):
                raise ValueError('WASM payload tag mismatch')
            expected = manifest['payload_sha256']
        else:
            if manifest.get('layer') != 'enigma-v1' or container['tag'] != module.wasm_tag(rotors):
                raise ValueError('Unsupported legacy format or wrong rotors')
            expected = request.get('expected_sha256')
            if not isinstance(expected, str) or len(expected) != 64:
                raise ValueError('Legacy decoding requires an independent expected_sha256')
        text = body.decode('ascii')
        if not text or text[0] not in '+~':
            raise ValueError('Invalid DNA anchor')
        sequence = module.dna_deanchors(text, catalog)
        if set(sequence) - set('ACGT') or len(sequence) % 4:
            raise ValueError('Invalid or truncated packed DNA')
        unwarped = module.warp.dewarp(module.dna.unpack(sequence), table)
        output = module.xor_stream(unwarped, hashlib.sha256(key + b'|L1').digest())
        if digest(output) != expected or version2 and len(output) != manifest['payload_bytes']:
            raise ValueError('Recovered payload checksum mismatch')
        target = output_dir / ('enigma-recovered-' + uuid.uuid4().hex + '.bin')
    else:
        raise ValueError('operation must be encode or decode')
    # Never write recovered output until every applicable format/hash check passes.
    with target.open('xb') as stream:
        stream.write(output)
    return {'ok': True, 'backend': 'fraymus-enigma-owner-code', 'adapter_format': 'fraynix-enigma-v2',
            'operation': operation, 'input': str(source), 'input_sha256': digest(original),
            'output': str(target.resolve()), 'output_sha256': digest(output), 'output_bytes': len(output),
            'integrity_verified': True, 'payload_executed': False, 'authenticated_encryption': False,
            'source_manifest_sha256': digest((root / 'source-intake.json').read_bytes())}


if __name__ == '__main__':
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument('--root', type=Path, required=True)
    parser.add_argument('--node', default='node')
    parser.add_argument('--output-dir', type=Path, required=True)
    parser.add_argument('request', type=Path)
    args = parser.parse_args()
    try:
        with contextlib.redirect_stdout(sys.stderr):
            result = transform(json.loads(args.request.read_text(encoding='utf-8')), args.root, args.output_dir, args.node)
        result['request_sha256'] = digest(args.request.read_bytes())
        print(json.dumps(result))
    except Exception as error:
        print(json.dumps({'ok': False, 'error': str(error)}))
        raise SystemExit(1)
