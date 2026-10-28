"""Pinned, resumable urllib fallback for environments where HF/Xet stalls."""
import concurrent.futures
import hashlib
import json
import shutil
import time
import urllib.request
from pathlib import Path
import argparse

REPO = "Lightricks/LTX-Video-0.9.1"
REVISION = "0dfb9a9b26f48256bbafc4e9fa337445ff76dc1c"


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", default="F:/Fraynix-Models/media/ltx-video-0.9.1")
    parser.add_argument("--repo", default=REPO)
    parser.add_argument("--revision", default=REVISION)
    args = parser.parse_args()
    root = Path(args.root).resolve()
    repo, revision = args.repo, args.revision
    with urllib.request.urlopen(f"https://huggingface.co/api/models/{repo}/revision/{revision}?blobs=true", timeout=60) as r:
        metadata = json.load(r)
    files = [f for f in metadata["siblings"] if not f["rfilename"].startswith(("media/", ".")) and ".fp16." not in f["rfilename"]]

    def download(item):
        name = item["rfilename"]
        target = (root / name).resolve()
        if not target.is_relative_to(root):
            raise ValueError("Unsafe repository path")
        target.parent.mkdir(parents=True, exist_ok=True)
        expected = item.get("size")
        part = target.with_suffix(target.suffix + ".part")
        if not target.exists() and not part.exists():
            previous = list((root / ".cache/huggingface/download" / Path(name).parent).glob(Path(name).name + ".*.incomplete"))
            if previous:
                largest = max(previous, key=lambda p: p.stat().st_size)
                if largest.stat().st_size: shutil.copyfile(largest, part)
        if expected and expected > 100_000_000 and not target.exists():
            chunks = root / ".parts" / name
            chunks.mkdir(parents=True, exist_ok=True)
            block = 16 * 1024 * 1024
            def fetch_chunk(start):
                end = min(expected, start + block) - 1
                chunk = chunks / str(start)
                if chunk.exists() and chunk.stat().st_size == end - start + 1: return
                for retry in range(8):
                    try:
                        url = f"https://huggingface.co/{repo}/resolve/{revision}/{name}?fraynix_chunk={start}-{end}"
                        request = urllib.request.Request(url, headers={"Range": f"bytes={start}-{end}"})
                        with urllib.request.urlopen(request, timeout=120) as response:
                            if response.status != 206 or not response.headers.get("Content-Range", "").startswith(f"bytes {start}-{end}/"):
                                raise ValueError("Invalid chunk range")
                            data = response.read()
                        if len(data) != end - start + 1: raise ValueError("Incomplete chunk")
                        chunk.write_bytes(data)
                        return
                    except Exception:
                        if retry == 7: raise
                        time.sleep(2)
            with concurrent.futures.ThreadPoolExecutor(max_workers=8) as ranges:
                list(ranges.map(fetch_chunk, range(0, expected, block)))
            with part.open("wb") as stream:
                for start in range(0, expected, block):
                    with (chunks / str(start)).open("rb") as chunk: shutil.copyfileobj(chunk, stream, 4 * 1024 * 1024)
            part.replace(target)
        for attempt in range(10):
            try:
                if target.exists() and (expected is None or target.stat().st_size == expected): break
                offset = part.stat().st_size if part.exists() else 0
                request = urllib.request.Request(f"https://huggingface.co/{repo}/resolve/{revision}/{name}?download=true",
                                                 headers={"Range": f"bytes={offset}-"} if offset else {})
                with urllib.request.urlopen(request, timeout=120) as response:
                    if offset and response.status != 206:
                        offset = 0
                    if offset and not response.headers.get("Content-Range", "").startswith(f"bytes {offset}-"):
                        raise ValueError("Incorrect resume offset")
                    with part.open("ab" if offset else "wb") as stream:
                        shutil.copyfileobj(response, stream, 4 * 1024 * 1024)
                if expected is not None and part.stat().st_size != expected:
                    raise ValueError("Incomplete download")
                part.replace(target)
                break
            except Exception as error:
                if attempt == 9: raise
                print(f"Resume {name}: {type(error).__name__}", flush=True)
                time.sleep(3)
        with target.open("rb") as stream:
            digest = hashlib.file_digest(stream, "sha256").hexdigest()
        expected_hash = item.get("lfs", {}).get("sha256")
        if expected_hash and digest != expected_hash:
            raise ValueError(f"Checksum mismatch: {name}")
        print(f"Verified {name}: {target.stat().st_size} bytes", flush=True)
        return {"file": name, "sha256": digest, "bytes": target.stat().st_size}

    with concurrent.futures.ThreadPoolExecutor(max_workers=2) as pool:
        records = list(pool.map(download, files))
    (root / "verified-manifest.json").write_text(json.dumps({"repo": repo, "revision": revision, "files": records}, indent=2))


if __name__ == "__main__":
    main()
