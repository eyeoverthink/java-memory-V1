"""Run the actual Fraynix shell against isolated learned-state directories."""
import json
import os
from pathlib import Path
import shutil
import subprocess
import time
import argparse

app = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument("--output", type=Path, required=True, help="An empty directory for isolated runtime state")
args = parser.parse_args()
root = args.output.resolve()
if root.exists() and any(root.iterdir()):
    raise SystemExit("Choose an empty output directory so existing state is not changed")
root.mkdir(parents=True, exist_ok=True)
output = root / "media_output"
output.mkdir(exist_ok=True)
(output / "settings.json").write_text(json.dumps({"automatic_voice": True, "playback": False, "dream_mode": "speech", "voice": "af_heart"}))
cp = (app / "build/media-runtime-classpath.txt").read_text()
cmd = [shutil.which("java"), "-Djava.awt.headless=true", "-Dfraynix.kronos.bootstrap.enabled=false",
       f"-Dfraynix.media.config={app / 'media/media-config.json'}", f"-Dfraynix.media.output={output}",
       "-cp", cp, "fraymus.FraynixBoot"]
with (root / "shell.log").open("w", encoding="utf-8") as log:
    process = subprocess.Popen(cmd, cwd=root, stdin=subprocess.PIPE, stdout=log, stderr=subprocess.STDOUT,
                               text=True, encoding="utf-8")
    try:
        until = time.monotonic() + 240
        while time.monotonic() < until:
            if process.poll() is not None: raise RuntimeError("Boot exited; inspect shell.log")
            if "fraynix>" in (root / "shell.log").read_text(encoding="utf-8", errors="replace"): break
            time.sleep(0.5)
        else: raise TimeoutError("Boot prompt timeout")
        process.stdin.write("media status\nvoice say Fraynix can now speak from its live shell.\nomni sleep\n")
        process.stdin.flush()
        until = time.monotonic() + 120
        while time.monotonic() < until:
            completed = []
            for path in output.glob("jobs/*/job.json"):
                value = json.loads(path.read_text())
                if value["status"] == "completed": completed.append(value)
            if len(completed) >= 2 and (output / "last-dream.json").exists(): break
            time.sleep(0.5)
        else: raise TimeoutError("Live speech/dream job timeout")
        process.stdin.write("omni wake\nmedia jobs\nexit\n")
        process.stdin.flush()
        process.wait(timeout=120)
        report = {"boot": True, "exit_code": process.returncode, "completed_jobs": len(completed),
                  "dream_source": json.loads((output / "last-dream.json").read_text()),
                  "speech_artifacts": [v["result"]["artifact"] for v in completed]}
        (root / "proof.json").write_text(json.dumps(report, indent=2))
        print(json.dumps(report))
        if process.returncode: raise RuntimeError("Nonzero shell exit")
    finally:
        if process.poll() is None:
            process.kill()
            process.wait()
