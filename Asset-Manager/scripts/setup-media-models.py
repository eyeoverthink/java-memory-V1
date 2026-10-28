"""Explicit online setup; inference workers never download models."""
import argparse
import hashlib
import json
import shutil
import urllib.request
import time
import os
from pathlib import Path


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", default="F:/Fraynix-Models/media")
    parser.add_argument("--video", action="store_true")
    parser.add_argument("--ltx", action="store_true", help="Optional large LTX download; inference is not yet validated")
    args = parser.parse_args()
    root = Path(args.root).resolve()
    root.mkdir(parents=True, exist_ok=True)
    records = []
    for name in ("kokoro-v1.0.onnx", "voices-v1.0.bin"):
        url = f"https://github.com/thewh1teagle/kokoro-onnx/releases/download/model-files-v1.0/{name}"
        target = root / "kokoro" / name
        target.parent.mkdir(parents=True, exist_ok=True)
        if not target.exists():
            temporary = target.with_suffix(target.suffix + ".part")
            with urllib.request.urlopen(url, timeout=120) as response, temporary.open("wb") as output:
                shutil.copyfileobj(response, output, 1024 * 1024)
            temporary.replace(target)
        records.append({"file": str(target), "source": url, "bytes": target.stat().st_size,
                        "sha256": hashlib.file_digest(target.open("rb"), "sha256").hexdigest()})
        print(f"Ready: {target}", flush=True)
    if args.video:
        import subprocess, sys
        subprocess.run([sys.executable, str(Path(__file__).with_name("download-ltx-http.py")),
                        "--repo", "guoyww/animatediff-motion-adapter-v1-5-2",
                        "--revision", "6167b88ffe39b4441fdf2113e77b99a6f56b7906",
                        "--root", str(root / "animatediff-v1-5-2")], check=True)
        records.append({"repo": "guoyww/animatediff-motion-adapter-v1-5-2",
                        "revision": "6167b88ffe39b4441fdf2113e77b99a6f56b7906"})
    if args.ltx:
        os.environ["HF_HUB_DISABLE_XET"] = "1"
        from huggingface_hub import HfApi, snapshot_download
        repo = "Lightricks/LTX-Video-0.9.1"
        revision = HfApi().model_info(repo).sha
        for attempt in range(6):
            try:
                target = snapshot_download(repo, revision=revision, local_dir=root / "ltx-video-0.9.1",
                                           ignore_patterns=["media/*", ".gitattributes"], max_workers=2)
                break
            except Exception as error:
                if attempt == 5: raise
                print(f"Download interrupted, resuming (attempt {attempt + 2}): {type(error).__name__}", flush=True)
                time.sleep(5)
        records.append({"repo": repo, "revision": revision, "directory": target})
    (root / "download-manifest.json").write_text(json.dumps(records, indent=2), encoding="utf-8")


if __name__ == "__main__":
    main()
