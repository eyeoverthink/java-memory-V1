"""Verify avatar script JSON bridges with fake scripts; never access a camera.

Requires only a JDK and Python. --existing-classes reproduces the crash using
the old application classes. Default mode compiles production sources afresh.
"""
import argparse
import hashlib
import json
from pathlib import Path
import shutil
import subprocess
import time

CAPTURE = r'''
import argparse, json
from pathlib import Path
p = argparse.ArgumentParser()
p.add_argument('--camera', type=int)
p.add_argument('--frames', type=int)
a, _ = p.parse_known_args()
with Path('capture-invocations.txt').open('a') as f: f.write(f'{a.camera},{a.frames}\n')
if a.camera == 0:
    print(json.dumps({'ok': False, 'message': 'fixture: OpenCV unavailable'}))
    raise SystemExit(1)
if a.camera == 2: print('{"ok": true, "message": "unterminated}')
elif a.camera == 3: print('[]')
elif a.camera == 4: print('{"ok": true} trailing')
elif a.camera == 5: print(json.dumps({'ok': True, 'interocular_distance': {}, 'grayscale_histogram': None}))
elif a.camera == 6: print('{"ok": true, "interocular_distance": NaN}')
elif a.camera == 7: print('{"ok": true, "ok": false}')
else:
    print(json.dumps({'ok': True, 'message': 'fixture \u03c6 at C:\\camera\\frame "one"',
        'interocular_distance': 0.125, 'symmetry_score': 0.9,
        'grayscale_histogram': [0.1, 0.2, 0.7], 'depth_profile': [0.2, 0.4],
        'thermal_profile': [0.3, 0.6], 'landmark_vector': [0.25, '0.5'],
        'blink_rate_hz': 0.2, 'depth_variance': 1e-7, 'parallax_score': 0.4,
        'texture_variance': 0.5, 'thermal_delta': 0.1, 'simulated': True}))
'''

SCAN = r'''
import argparse, json
p = argparse.ArgumentParser()
p.add_argument('--file')
a = p.parse_args()
if a.file == 'failure.png': print('{"ok":false,"message":"fixture scan unavailable"}')
elif a.file == 'malformed.png': print('{"ok":true,}')
elif a.file == 'wrong-images.png': print('{"ok":true,"images":{}}')
elif a.file == 'empty.png': print('{"ok":true}')
else:
    print(json.dumps({'ok': True, 'message': 'fixture scan', 'overall_risk': 0.2,
        'confidence': 0.8, 'images': [{'source': a.file, 'faces': 1, 'risk': 0.2,
        'notes': ['fixture one', 'fixture two'], 'provenance_signals': ['local fixture']}]}))
'''


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output', type=Path, required=True)
    parser.add_argument('--existing-classes', type=Path)
    args = parser.parse_args()
    project = Path(__file__).resolve().parents[1]
    output = args.output.resolve()
    if output.exists():
        parser.error('Output must be new so existing classes cannot mask results')
    javac, java = shutil.which('javac'), shutil.which('java')
    if not javac or not java:
        parser.error('java and javac are required on PATH')
    classes, fixture = output / 'classes', output / 'fixture'
    classes.mkdir(parents=True)
    (fixture / 'scripts/avatar').mkdir(parents=True)
    (fixture / 'AVATAR_JSON_TEST_FIXTURE').write_text('Fake capture scripts only.\n')
    (fixture / 'scripts/avatar/opencv_capture.py').write_text(CAPTURE, encoding='utf-8')
    (fixture / 'scripts/avatar/deepfake_scan.py').write_text(SCAN, encoding='utf-8')
    for name in ['ok.png', 'failure.png', 'malformed.png', 'wrong-images.png', 'empty.png']:
        (fixture / name).write_bytes(b'fixture, not an image')
    source = project / 'src/main/java'
    tests = project / 'src/test/java'
    names = ['fraymus.identity.AvatarJsonBridgeRegressionTest']
    sources = [tests / 'fraymus/identity/AvatarJsonBridgeRegressionTest.java']
    classpath = str(classes)
    if args.existing_classes:
        classpath += ';' + str(args.existing_classes.resolve())
    else:
        names.append('fraymus.core.FraymusJsonStrictTest')
        sources += [tests / 'fraymus/core/FraymusJsonStrictTest.java']
        sources += [source / p for p in ['fraymus/core/FraymusJSON.java',
            'fraymus/identity/OpenCvFaceAbstraction.java',
            'fraymus/identity/DeepfakeScanService.java',
            'fraymus/identity/BiometricAvatarService.java']]

    def run(name, command):
        start = time.time()
        with (output / (name + '.log')).open('wb') as log:
            try:
                p = subprocess.run(command, cwd=fixture, stdout=log,
                    stderr=subprocess.STDOUT, timeout=120)
                code = p.returncode
            except subprocess.TimeoutExpired:
                code = 124
                log.write(b'\nTimed out after 120 seconds.\n')
        r = {'name': name, 'command': command, 'exit_code': code,
             'seconds': round(time.time() - start, 2), 'log': str(output / (name + '.log'))}
        print(json.dumps(r), flush=True)
        return r

    compile_result = run('compile', [javac, '-proc:none', '-encoding', 'UTF-8',
        '--release', '21', '-classpath', classpath, '-sourcepath', str(source) if not args.existing_classes else str(classes),
        '-d', str(classes), *map(str, sources)])
    results = []
    if compile_result['exit_code'] == 0:
        for name in names:
            results.append(run(name, [java, '-cp', classpath, name]))
    record = {'mode': 'existing classes' if args.existing_classes else 'fresh source',
        'compile': compile_result, 'tests': results,
        'source_sha256': {p.relative_to(project).as_posix(): hashlib.sha256(p.read_bytes()).hexdigest() for p in sources},
        'fixture': str(fixture), 'camera_used': False, 'network_used_by_fixtures': False}
    (output / 'results.json').write_text(json.dumps(record, indent=2), encoding='utf-8')
    return 0 if compile_result['exit_code'] == 0 and all(r['exit_code'] == 0 for r in results) else 1


if __name__ == '__main__':
    raise SystemExit(main())
