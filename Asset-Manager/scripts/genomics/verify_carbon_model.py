"""Check the installed model's chunked scores against stock Llama logits."""
import argparse
import contextlib
import json
from pathlib import Path
import sys
import time
from carbon_worker import CarbonEngine, DEFAULT_CONFIG, load_config


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--config', type=Path, default=DEFAULT_CONFIG)
    parser.add_argument('--output', type=Path, required=True)
    args = parser.parse_args()
    if args.output.exists():
        parser.error('Output must be new')
    started = time.monotonic()
    with contextlib.redirect_stdout(sys.stderr):
        engine = CarbonEngine(load_config(args.config))
        torch = engine.torch
        sequence = 'ATGCGCTAGCTACGATCGATCGTAGCTAGCTAGCTAGCTACG'
        ids, used = engine.codec.encode(sequence)
        tensor = torch.tensor([ids], device=engine.model.get_input_embeddings().weight.device)
        with torch.inference_mode():
            logits = engine.model(input_ids=tensor, use_cache=False).logits[0, :-1].float()
            target = torch.tensor(ids[1:], device=logits.device)
            expected = (logits.gather(1, target[:, None]).squeeze(1)
                        - torch.logsumexp(logits, dim=1)).double().mean().item()
        actual = engine.score(sequence)['mean_log_probability_per_6mer']
        error = abs(actual - expected)
        if error > 0.005:
            raise AssertionError(f'Chunked score differs from stock model forward: {error}')
        generated, decoding = engine.generate(sequence, 24)
        if len(generated) != used + 24 or set(generated) - set('ACGT'):
            raise AssertionError('DNA continuation length or alphabet mismatch')
    result = {'ok': True, 'engine': engine.details, 'stock_forward_mean_logp': expected,
              'chunked_mean_logp': actual, 'absolute_error': error,
              'generation': decoding, 'seconds': round(time.monotonic() - started, 2)}
    args.output.parent.mkdir(parents=True, exist_ok=True)
    with args.output.open('x', encoding='utf-8') as stream:
        json.dump(result, stream, indent=2)
    print(json.dumps(result))


if __name__ == '__main__':
    main()
