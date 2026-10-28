"""Fetch the pinned Apache-2.0 Needle3 weights and Windows engine; no SDK installation."""
import argparse
import hashlib
import json
from pathlib import Path
import urllib.request
import zipfile

REVISION = 'd6c7d6a2e9655505e0f8e23339356dd1705900eb'
ARTIFACTS = {
    'needle3.cact': 'c9d915eca282ed42d1a09b143b592adb4cc6744ffe2d294adf5cfc5548170c38',
    'python/cactus_needle-3.3.3-py3-none-win_amd64.whl': 'b6447b2be7426b7ff3b06360b15a90a8b7870e3fa2275023e20541d5e8d81bdb',
}


def install(root):
    root.mkdir(parents=True, exist_ok=True)
    captured = []
    for name, expected in ARTIFACTS.items():
        path = root / Path(name).name
        url = f'https://huggingface.co/Cactus-Compute/needle3/resolve/{REVISION}/{name}'
        if not path.exists():
            temporary = path.with_suffix(path.suffix + '.partial')
            with urllib.request.urlopen(url, timeout=120) as source, temporary.open('wb') as target:
                while chunk := source.read(1024 * 1024):
                    target.write(chunk)
            if hashlib.sha256(temporary.read_bytes()).hexdigest() != expected:
                raise ValueError('Downloaded artifact checksum mismatch: ' + name)
            temporary.replace(path)
        if hashlib.sha256(path.read_bytes()).hexdigest() != expected:
            raise ValueError('Installed artifact checksum mismatch: ' + name)
        captured.append({'source_url': url, 'path': str(path.resolve()), 'sha256': expected})
    with zipfile.ZipFile(root / 'cactus_needle-3.3.3-py3-none-win_amd64.whl') as wheel:
        dll = wheel.read('needle/libneedle3.dll')
    library = root / 'libneedle3.dll'
    library.write_bytes(dll)
    config = {'model': 'Cactus-Compute/needle3', 'revision': REVISION,
              'engine_version': '3.3.3', 'python': __import__('sys').executable,
              'weights': str((root / 'needle3.cact').resolve()), 'weights_sha256': ARTIFACTS['needle3.cact'],
              'library': str(library.resolve()), 'library_sha256': hashlib.sha256(dll).hexdigest(),
              'license': 'Apache-2.0', 'artifacts': captured, 'network_required_for_inference': False}
    (root / 'installation.json').write_text(json.dumps(config, indent=2), encoding='utf-8')
    return config


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('root', type=Path)
    args = parser.parse_args()
    print(json.dumps(install(args.root.resolve())))
