"""Download or verify the pinned Carbon-3B safetensors and write its local manifest."""
import argparse
from datetime import datetime, timezone
import hashlib
import json
import os
from pathlib import Path

MODEL = 'HuggingFaceBio/Carbon-3B'
REVISION = '07c88a21cbb8a18a442baf651d896aba6057979d'
WEIGHTS = {
    'model-00001-of-00003.safetensors': 'e497fa3a3b7df30eca9231a3def3fd0742fa2dbaf098043342f3a6cb7f26593c',
    'model-00002-of-00003.safetensors': 'c843313dbf2775fe2d89496c296ad6254437c25713ce913266a6e526949125ea',
    'model-00003-of-00003.safetensors': '4ab9845411f31996bc54be74d8078ad3e28bb30b95462dde62a78261e1bf067c',
}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--model-dir', type=Path, required=True)
    parser.add_argument('--verify-only', action='store_true')
    args = parser.parse_args()
    root = args.model_dir.resolve()
    if not args.verify_only:
        os.environ['HF_HUB_DISABLE_XET'] = '1'
        from huggingface_hub import snapshot_download
        snapshot_download(MODEL, revision=REVISION, local_dir=root,
                          allow_patterns=['*.json', '*.safetensors', 'tokenizer.py', 'README.md'], max_workers=3)
    files = []
    required = [*WEIGHTS, 'config.json', 'dna_config.json', 'model.safetensors.index.json', 'README.md']
    for name in required:
        path = root / name
        digest = hashlib.sha256()
        with path.open('rb') as stream:
            for block in iter(lambda: stream.read(8 * 1024 * 1024), b''):
                digest.update(block)
        actual = digest.hexdigest()
        if name in WEIGHTS and actual != WEIGHTS[name]:
            raise ValueError(f'Weight hash mismatch: {name}')
        files.append({'name': name, 'bytes': path.stat().st_size, 'sha256': actual})
    manifest = {'model': MODEL, 'revision': REVISION, 'verified': True,
                'verified_utc': datetime.now(timezone.utc).isoformat(), 'license': 'apache-2.0',
                'source': 'https://huggingface.co/HuggingFaceBio/Carbon-3B', 'files': files}
    (root / 'installation.json').write_text(json.dumps(manifest, indent=2), encoding='utf-8')
    print(json.dumps({'ok': True, 'path': str(root), 'revision': REVISION,
                      'verified_weight_files': len(WEIGHTS), 'bytes': sum(x['bytes'] for x in files)}))


if __name__ == '__main__':
    main()
