import os
import sys

os.environ.setdefault("TRANSFORMERS_OFFLINE", "1")
os.environ.setdefault("HF_DATASETS_OFFLINE", "1")
os.environ.setdefault("HF_HUB_DISABLE_PROGRESS_BARS", "1")
os.environ.setdefault("TOKENIZERS_PARALLELISM", "false")


def main() -> int:
    if len(sys.argv) < 4:
        print("usage: local_transformers_runtime.py <bundle_root> <prompt> <max_tokens>", file=sys.stderr)
        return 2

    bundle_root = sys.argv[1]
    prompt = sys.argv[2]
    max_tokens = int(sys.argv[3])

    from transformers import AutoModelForCausalLM, AutoTokenizer, logging as transformers_logging
    import torch

    transformers_logging.set_verbosity_error()
    tokenizer = AutoTokenizer.from_pretrained(bundle_root, local_files_only=True)
    model = AutoModelForCausalLM.from_pretrained(bundle_root, local_files_only=True)

    if tokenizer.pad_token_id is None and tokenizer.eos_token is not None:
        tokenizer.pad_token = tokenizer.eos_token

    inputs = tokenizer(prompt, return_tensors="pt")
    with torch.no_grad():
        output_ids = model.generate(
            **inputs,
            max_new_tokens=max_tokens,
            do_sample=False,
            pad_token_id=tokenizer.pad_token_id if tokenizer.pad_token_id is not None else tokenizer.eos_token_id,
        )

    prefix_length = inputs["input_ids"].shape[-1]
    generated = tokenizer.decode(output_ids[0][prefix_length:], skip_special_tokens=True).strip()
    if not generated:
        generated = tokenizer.decode(output_ids[0], skip_special_tokens=True).strip()
    print(generated)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
