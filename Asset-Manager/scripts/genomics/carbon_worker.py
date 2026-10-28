"""Local Carbon-3B DNA analysis and generation for the Java Fraynix shell.

FASTA inventory needs only the Python standard library. Inference loads the
pinned, local safetensors with stock Transformers Llama code. The pure DNA
6-mer codec follows Carbon's published ATCG ordering and dna_config.json;
neither remote tokenizer code nor a second Qwen model is needed.
"""
import argparse
import collections
import contextlib
from datetime import datetime, timezone
import hashlib
import itertools
import json
import math
import os
from pathlib import Path
import random
import re
import sys
import time
import uuid
from reference_catalog import ReferenceCatalog

MODEL_ID = 'HuggingFaceBio/Carbon-3B'
REVISION = '07c88a21cbb8a18a442baf651d896aba6057979d'
PROJECT = Path(__file__).resolve().parents[2]
DEFAULT_CONFIG = Path.home() / '.fraymus/models/carbon-runtime.json'


def sha256(path):
    digest = hashlib.sha256()
    with Path(path).open('rb') as stream:
        for block in iter(lambda: stream.read(8 * 1024 * 1024), b''):
            digest.update(block)
    return digest.hexdigest()


def load_config(path):
    config = json.loads(path.read_text(encoding='utf-8-sig')) if path.is_file() else {}
    if os.environ.get('FRAYNIX_CARBON_MODEL'):
        config['model_path'] = os.environ['FRAYNIX_CARBON_MODEL']
    return config


class DnaCodec:
    """Exact canonical DNA subset of Carbon's hybrid tokenizer."""
    def __init__(self, configuration):
        self.k = int(configuration['k'])
        if self.k != 6:
            raise ValueError('This adapter requires the Carbon 6-mer vocabulary')
        self.begin = int(configuration['dna_start_id'])
        self.end = self.begin + 1
        self.first = self.begin + 3
        self.kmers = [''.join(x) for x in itertools.product('ATCG', repeat=self.k)]
        self.ids = {word: self.first + i for i, word in enumerate(self.kmers)}

    def encode(self, sequence, close=False):
        if re.fullmatch('[ACGT]+', sequence) is None:
            raise ValueError('Carbon DNA inputs must contain uppercase A/C/G/T only')
        aligned = len(sequence) // self.k * self.k
        if aligned == 0:
            raise ValueError('At least one complete 6-mer is required')
        ids = [self.begin] + [self.ids[sequence[i:i + self.k]] for i in range(0, aligned, self.k)]
        return ids + ([self.end] if close else []), aligned

    def decode(self, ids):
        return ''.join(self.kmers[value - self.first] for value in ids
                       if self.first <= value < self.first + len(self.kmers))


def fasta_records(path):
    header, bases = None, []
    for line in path.read_text(encoding='utf-8-sig').splitlines():
        line = line.strip()
        if not line or line.startswith(';'):
            continue
        if line.startswith('>'):
            if header is not None:
                yield header, ''.join(bases).upper()
            header, bases = line[1:], []
        else:
            if header is None:
                raise ValueError(f'{path}: FASTA sequence precedes its header')
            bases.append(''.join(line.split()))
    if header is not None:
        yield header, ''.join(bases).upper()
    else:
        raise ValueError(f'{path}: no FASTA records')


def label_encoding(label, legacy_java_hash=False):
    mask, seed = (1 << 64) - 1, 0
    # Java's generator iterates UTF-16 code units.
    raw = label.encode('utf-16-be')
    for i in range(0, len(raw), 2):
        seed = (31 * seed + int.from_bytes(raw[i:i + 2], 'big')) & mask
    if legacy_java_hash:
        seed &= 0xffffffff
        if seed >= 0x80000000:
            seed = (seed - 0x100000000) & mask
    output = []
    for _ in range(256):
        seed = (seed + 0x9e3779b97f4a7c15) & mask
        word = seed
        word = ((word ^ (word >> 30)) * 0xbf58476d1ce4e5b9) & mask
        word = ((word ^ (word >> 27)) * 0x94d049bb133111eb) & mask
        word ^= word >> 31
        output.extend('ACGT'[(word >> bit) & 3] for bit in range(0, 64, 2))
    return ''.join(output)


def record_stats(path, header, sequence):
    counts = collections.Counter(sequence)
    length = len(sequence)
    invalid = sorted(set(sequence) - set('ACGT'))
    label = header.split('|')[0].strip()
    current_match = length == 8192 and not invalid and sequence == label_encoding(label)
    legacy_match = length == 8192 and not invalid and sequence == label_encoding(label, True)
    return {'path': str(path.resolve()), 'file_sha256': sha256(path), 'header': header,
            'sequence_sha256': hashlib.sha256(sequence.encode('utf-8')).hexdigest(),
            'bases': length, 'alphabet': sorted(counts), 'invalid_symbols': invalid,
            'counts': dict(sorted(counts.items())),
            'gc_percent': 100 * (counts['G'] + counts['C']) / length if length else None,
            'base_entropy_bits': -sum((n / length) * math.log2(n / length) for n in counts.values()) if length else 0,
            'matches_label_seeded_apotheosis_vector': current_match,
            'matches_legacy_java_hash_vector': legacy_match,
            'encoding_provenance': 'label-seeded vector encoding' if current_match or legacy_match else 'unresolved',
            'functional_annotation': 'unestablished; no sequence-function assay or reference alignment performed'}


def find_fastas(root):
    skip = {'.git', '.gradle', 'build', 'node_modules', '.codex-temp', '.northstar-kernel',
            'release-capsules', 'knowledge_base', '__pycache__'}
    paths = []
    for parent, directories, names in os.walk(root):
        directories[:] = [name for name in directories if name not in skip]
        paths.extend(Path(parent) / name for name in names if Path(name).suffix.lower() in {'.fasta', '.fa', '.fna'})
    return sorted(paths)


class CarbonEngine:
    def __init__(self, config):
        os.environ['HF_HUB_OFFLINE'] = '1'
        os.environ['TRANSFORMERS_OFFLINE'] = '1'
        os.environ['HF_HUB_DISABLE_TELEMETRY'] = '1'
        import torch
        from transformers import AutoModelForCausalLM
        self.torch = torch
        self.path = Path(config['model_path']).resolve()
        installation = json.loads((self.path / 'installation.json').read_text(encoding='utf-8'))
        if installation.get('revision') != REVISION or not installation.get('verified'):
            raise ValueError('Pinned model installation has not passed its file checks')
        self.codec = DnaCodec(json.loads((self.path / 'dna_config.json').read_text()))
        model_config = json.loads((self.path / 'config.json').read_text())
        self.context = int(model_config['max_position_embeddings'])
        if model_config['vocab_size'] < self.codec.first + 4096:
            raise ValueError('Model and DNA vocabulary do not match')
        torch.set_num_threads(int(config.get('cpu_threads', 8)))
        self.chunk = int(config.get('score_chunk_tokens', 64))
        if self.chunk <= 0:
            raise ValueError('score_chunk_tokens must be positive')
        kwargs = {'local_files_only': True, 'trust_remote_code': False,
                  'attn_implementation': 'sdpa', 'low_cpu_mem_usage': True}
        device = config.get('device', 'auto')
        if torch.cuda.is_available() and device != 'cpu':
            free, _ = torch.cuda.mem_get_info(0)
            available = int(free * float(config.get('cuda_free_memory_fraction', 0.80)))
            dtype = torch.bfloat16 if torch.cuda.is_bf16_supported() else torch.float16
            kwargs.update(dtype=dtype, device_map='auto',
                          max_memory={0: available, 'cpu': int(config.get('cpu_memory_gib', 24)) * 1024 ** 3})
        else:
            kwargs.update(dtype=torch.float32, device_map='cpu')
        self.model = AutoModelForCausalLM.from_pretrained(str(self.path), **kwargs).eval()
        self.details = {'model': MODEL_ID, 'revision': REVISION, 'model_path': str(self.path),
                        'installation_manifest_sha256': sha256(self.path / 'installation.json'),
                        'dtype': str(self.model.dtype), 'device_map': getattr(self.model, 'hf_device_map', {'model': str(self.model.device)}),
                        'torch': torch.__version__, 'cuda_available': torch.cuda.is_available(),
                        'codec': 'canonical DNA 6-mer subset; ATCG lexicographic order; explicit <dna> token'}

    def score(self, sequence):
        torch = self.torch
        ids, aligned = self.codec.encode(sequence)
        if len(ids) > self.context:
            raise ValueError(f'Sequence needs {len(ids)} tokens; model context is {self.context}')
        tensor = torch.tensor([ids], device=self.model.get_input_embeddings().weight.device)
        # Compute the transformer once, then project the vocabulary in small
        # chunks. This avoids retaining an entire sequence x 155,776 logit array.
        with torch.inference_mode():
            hidden = self.model.model(input_ids=tensor, use_cache=False).last_hidden_state[0]
            total, count = 0.0, 0
            for start in range(0, len(ids) - 1, self.chunk):
                stop = min(start + self.chunk, len(ids) - 1)
                logits = self.model.lm_head(hidden[start:stop]).float()
                target = torch.tensor(ids[start + 1:stop + 1], device=logits.device)
                observed = logits.gather(1, target[:, None]).squeeze(1) - torch.logsumexp(logits, dim=1)
                total += observed.double().sum().item()
                count += len(target)
                del logits, observed
        mean = total / count
        return {'mean_log_probability_per_6mer': mean, 'perplexity_per_6mer': math.exp(-mean),
                'scored_tokens': count, 'scored_bases': aligned, 'unscored_tail_bases': len(sequence) - aligned,
                'score_scope': 'DNA tokens only, full-vocabulary likelihood; no end-tag score',
                'biological_function_proven': False}

    def generate(self, primer, bases):
        from transformers import LogitsProcessor, LogitsProcessorList
        torch, codec = self.torch, self.codec
        ids, aligned = codec.encode(primer)
        tokens = math.ceil(bases / codec.k)
        if bases <= 0 or len(ids) + tokens > self.context:
            raise ValueError('Requested continuation must be positive and fit the native model context')

        class CanonicalDna(LogitsProcessor):
            def __call__(self, input_ids, scores):
                scores[:, :codec.first] = -float('inf')
                scores[:, codec.first + 4096:] = -float('inf')
                return scores

        tensor = torch.tensor([ids], device=self.model.get_input_embeddings().weight.device)
        with torch.inference_mode():
            result = self.model.generate(input_ids=tensor, attention_mask=torch.ones_like(tensor),
                max_new_tokens=tokens, do_sample=False, use_cache=True,
                logits_processor=LogitsProcessorList([CanonicalDna()]),
                pad_token_id=codec.end, eos_token_id=None)
        continuation = codec.decode(result[0, len(ids):].tolist())[:bases]
        if len(continuation) != bases:
            raise RuntimeError('Model returned an incomplete canonical DNA continuation')
        return primer[:aligned] + continuation, {'primer_bases_used': aligned,
            'primer_tail_excluded': len(primer) - aligned, 'new_bases': len(continuation),
            'decoding': 'greedy, restricted to canonical DNA 6-mer tokens'}


def save_report(output, kind, body):
    output.mkdir(parents=True, exist_ok=True)
    stamp = datetime.now(timezone.utc).strftime('%Y%m%dT%H%M%SZ') + '-' + uuid.uuid4().hex[:8]
    path = output / (kind + '-' + stamp + '.json')
    body = {'created_utc': datetime.now(timezone.utc).isoformat(), **body}
    with path.open('x', encoding='utf-8') as stream:
        json.dump(body, stream, indent=2, allow_nan=False)
    return path


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--config', type=Path, default=Path(os.environ.get('FRAYNIX_CARBON_CONFIG', DEFAULT_CONFIG)))
    parser.add_argument('--output-dir', type=Path, default=PROJECT / 'reports/genomics')
    parser.add_argument('--state-dir', type=Path, default=PROJECT / '.genomics')
    commands = parser.add_subparsers(dest='command', required=True)
    commands.add_parser('status')
    inventory = commands.add_parser('inventory')
    inventory.add_argument('root', nargs='?', type=Path, default=PROJECT)
    analyze = commands.add_parser('analyze')
    analyze.add_argument('paths', nargs='+', type=Path)
    analyze.add_argument('--compare-shuffled', action='store_true')
    generate = commands.add_parser('generate')
    generate.add_argument('path', type=Path)
    generate.add_argument('--bases', type=int, default=120)
    commands.add_parser('learn')
    absorb = commands.add_parser('absorb')
    absorb.add_argument('path', type=Path)
    absorb.add_argument('--annotation', type=Path, required=True)
    absorb.add_argument('--source-url', required=True)
    identify = commands.add_parser('identify')
    identify.add_argument('path', type=Path)
    args = parser.parse_args()
    config = load_config(args.config)
    catalog = ReferenceCatalog(args.state_dir / 'references')
    if args.command == 'absorb':
        result = catalog.absorb(args.path, args.annotation, args.source_url, fasta_records(args.path))
        report = save_report(args.output_dir, 'reference-absorption', {'kind': 'reference_absorption', **result})
        print(json.dumps({'ok': True, 'report': str(report), **result}))
        return
    if args.command == 'identify':
        records = [{**record_stats(args.path, header, sequence), 'reference_identity': catalog.identify(sequence)}
                   for header, sequence in fasta_records(args.path)]
        report = save_report(args.output_dir, 'reference-identification', {'kind': 'reference_identification', 'records': records})
        print(json.dumps({'ok': True, 'report': str(report), 'records': records, 'model_inference_used': False}))
        return
    if args.command == 'status':
        path = Path(config['model_path']) if config.get('model_path') else None
        installed = bool(path and (path / 'installation.json').is_file())
        print(json.dumps({'ok': True, 'model': MODEL_ID, 'revision': REVISION,
            'configured': bool(config), 'installed': installed, 'config': str(args.config),
            'model_path': str(path) if path else None, 'python': sys.executable,
            'analysis_report_directory': str(args.output_dir)}))
        return
    if args.command == 'learn':
        path = PROJECT / 'skills/open_model_sources/generated/carbon-3b.md'
        skill = PROJECT / 'skills/genomics_carbon/SKILL.md'
        print(json.dumps({'ok': True, 'model': MODEL_ID, 'revision': REVISION,
            'knowledge_source': str(path), 'knowledge_sha256': sha256(path),
            'knowledge': path.read_text(encoding='utf-8'), 'skill_source': str(skill),
            'skill_sha256': sha256(skill), 'skill': skill.read_text(encoding='utf-8'),
            'mode': 'retrieval knowledge; model weights unchanged'}))
        return
    if args.command == 'inventory':
        records = [record_stats(path, header, sequence) for path in find_fastas(args.root)
                   for header, sequence in fasta_records(path)]
        report = save_report(args.output_dir, 'inventory', {'kind': 'fasta_inventory', 'root': str(args.root.resolve()),
            'records': records, 'model_inference_used': False})
        print(json.dumps({'ok': True, 'report': str(report), 'records': len(records),
            'label_encoding_matches': sum(r['matches_label_seeded_apotheosis_vector'] for r in records),
            'known_vector_encoding_matches': sum(r['encoding_provenance'] != 'unresolved' for r in records),
            'summary': [{'name': Path(r['path']).name, 'bases': r['bases'], 'gc_percent': r['gc_percent'],
                         'label_encoding_match': r['matches_label_seeded_apotheosis_vector'],
                         'legacy_hash_match': r['matches_legacy_java_hash_vector']} for r in records]}))
        return
    if not config.get('model_path'):
        raise ValueError(f'Carbon runtime not configured: {args.config}')
    started = time.monotonic()
    with contextlib.redirect_stdout(sys.stderr):
        engine = CarbonEngine(config)
    if args.command == 'analyze':
        paths = []
        for path in args.paths:
            paths.extend(find_fastas(path) if path.is_dir() else [path])
        records = []
        for path in dict.fromkeys(paths):
            for header, sequence in fasta_records(path):
                record = record_stats(path, header, sequence)
                record['reference_identity'] = catalog.identify(sequence)
                print(f'Carbon scoring {path.name}: {len(sequence)} bases', file=sys.stderr, flush=True)
                record['carbon'] = engine.score(sequence)
                if args.compare_shuffled:
                    aligned = sequence[:len(sequence) // 6 * 6]
                    shuffled = list(aligned)
                    random.Random(42).shuffle(shuffled)
                    shuffled = ''.join(shuffled)
                    record['composition_matched_shuffle'] = engine.score(shuffled)
                    record['mean_logp_delta_vs_shuffle'] = record['carbon']['mean_log_probability_per_6mer'] - record['composition_matched_shuffle']['mean_log_probability_per_6mer']
                records.append(record)
        report = save_report(args.output_dir, 'carbon-analysis', {'kind': 'carbon_likelihood_analysis',
            'engine': engine.details, 'records': records, 'seconds': round(time.monotonic() - started, 2)})
        print(json.dumps({'ok': True, 'report': str(report), 'records': len(records),
            'engine': engine.details, 'scores': [{'name': Path(r['path']).name,
                'mean_logp_per_6mer': r['carbon']['mean_log_probability_per_6mer'],
                'reference_identity': r['reference_identity'],
                'delta_vs_shuffle': r.get('mean_logp_delta_vs_shuffle')} for r in records]}))
    else:
        records = list(fasta_records(args.path))
        if len(records) != 1:
            raise ValueError('Generation requires a FASTA file containing exactly one primer record')
        header, primer = records[0]
        sequence, decoding = engine.generate(primer, args.bases)
        args.output_dir.mkdir(parents=True, exist_ok=True)
        fasta = args.output_dir / ('carbon-continuation-' + uuid.uuid4().hex + '.fasta')
        with fasta.open('x', encoding='ascii') as stream:
            stream.write('>FRAYNIX_CARBON_CONTINUATION | unvalidated computational sequence\n')
            stream.write('\n'.join(sequence[i:i + 80] for i in range(0, len(sequence), 80)) + '\n')
        report = save_report(args.output_dir, 'carbon-generation', {'kind': 'carbon_dna_continuation',
            'engine': engine.details, 'primer': record_stats(args.path, header, primer),
            'output': str(fasta), 'output_sha256': sha256(fasta), 'decoding': decoding,
            'functional_validation': 'not performed', 'seconds': round(time.monotonic() - started, 2)})
        print(json.dumps({'ok': True, 'report': str(report), 'fasta': str(fasta), 'decoding': decoding,
            'engine': engine.details}))


if __name__ == '__main__':
    try:
        main()
    except Exception as error:
        print(json.dumps({'ok': False, 'error': str(error), 'error_type': type(error).__name__}), flush=True)
        raise SystemExit(1)
