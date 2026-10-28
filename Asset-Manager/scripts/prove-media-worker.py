"""Run a real local inference request and keep its evidence outside live state."""
import argparse
import json
import subprocess
import os
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument("action", choices=["speech", "image", "video", "animation"])
parser.add_argument("--output", required=True)
parser.add_argument("--models", default=os.environ.get("FRAYNIX_MODEL_ROOT", "F:/Fraynix-Models"))
args = parser.parse_args()
app = Path(__file__).resolve().parents[1]
cfg = json.loads((app / "media/media-config.json").read_text())["backends"][args.action]
cfg = {k: v.replace("${MODEL_ROOT}", args.models) if isinstance(v, str) else v for k, v in cfg.items()}
out = Path(args.output).resolve()
out.mkdir(parents=True, exist_ok=True)
text = "I am Fraynix. My local speech output is connected to my brain system."
if args.action in ("image", "video"):
    text = "A green apple resting on a wooden table, sunlight through a window, gentle camera movement, realistic."
req = {"id": "local-proof-" + args.action, "action": args.action, "text": text,
       "source": {"kind": "integration_test", "component": "media_worker"},
       "output_dir": str(out), "backend_config": cfg,
       "options": {"width": 256, "height": 256, "steps": 8, "frames": 9, "seed": 42}}
path = out / "request.json"
path.write_text(json.dumps(req), encoding="utf-8")
with (out / "worker.log").open("w", encoding="utf-8") as log:
    env=dict(os.environ, PYTHONNOUSERSITE="1")
    env.pop("PYTHONHOME", None); env.pop("PYTHONPATH", None)
    result = subprocess.run([cfg["python"], str(app / "media/media_worker.py"), "--request", str(path)],
                            stdout=log, stderr=subprocess.STDOUT, env=env)
if (out / "result.json").exists():
    print((out / "result.json").read_text(encoding="utf-8"))
raise SystemExit(result.returncode)
