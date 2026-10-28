"""Verify model discovery and Ollama JSON protocols without third-party JARs.

Requires Python and a JDK 21 or newer. HTTP traffic goes only to fixture servers
on ephemeral loopback ports. Existing catalogs, models and learned state are not
modified. Fresh mode compiles the production sources and their dependencies.
"""
import argparse
import hashlib
import json
from pathlib import Path
import shutil
import subprocess
import time


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output', required=True, type=Path)
    parser.add_argument('--existing-classes', type=Path)
    args = parser.parse_args()
    project = Path(__file__).resolve().parents[1]
    output = args.output.resolve()
    if output.exists():
        parser.error('Output must be new so stale files cannot mask results')
    if not shutil.which('javac') or not shutil.which('java'):
        parser.error('java and javac are required on PATH')
    classes = output / 'classes'
    fixture = output / 'fixture'
    classes.mkdir(parents=True)
    fixture.mkdir()
    source = project / 'src/main/java'
    tests = project / 'src/test/java'
    sources = [tests / 'fraymus/OllamaJsonStartupRegressionTest.java',
               tests / 'fraymus/core/FraymusJsonStrictTest.java']
    classpath = str(classes)
    if args.existing_classes:
        classpath += ';' + str(args.existing_classes.resolve())
    else:
        sources += [source / path for path in ['fraymus/OllamaIntegration.java',
                    'fraymus/llm/LocalModelCatalog.java', 'fraymus/core/OllamaBridge.java',
                    'fraymus/core/FraymusJSON.java']]

    def run(name, command):
        started = time.monotonic()
        with (output / (name + '.log')).open('wb') as log:
            try:
                process = subprocess.run(command, cwd=fixture, stdout=log,
                                         stderr=subprocess.STDOUT, timeout=120)
                code = process.returncode
            except subprocess.TimeoutExpired:
                code = 124
                log.write(b'\nTimed out after 120 seconds.\n')
        record = {'name': name, 'command': command, 'exit_code': code,
                  'seconds': round(time.monotonic() - started, 2),
                  'log': str(output / (name + '.log'))}
        print(json.dumps(record), flush=True)
        return record

    compiled = run('compile', ['javac', '-proc:none', '-encoding', 'UTF-8',
        '--release', '21', '-classpath', classpath,
        '-sourcepath', str(classes) if args.existing_classes else str(source),
        '-d', str(classes), *map(str, sources)])
    results = []
    if compiled['exit_code'] == 0:
        results.append(run('ollama', ['java', '-cp', classpath,
            'fraymus.OllamaJsonStartupRegressionTest', str(fixture)]))
        results.append(run('strict-json', ['java', '-cp', classpath,
            'fraymus.core.FraymusJsonStrictTest']))
    inputs = set(sources)
    if not args.existing_classes:
        for path in classes.rglob('*.class'):
            outer = path.relative_to(classes).as_posix().split('$')[0].removesuffix('.class')
            candidate = source / (outer + '.java')
            if candidate.exists():
                inputs.add(candidate)
    record = {'mode': 'existing classes' if args.existing_classes else 'fresh source',
              'compile': compiled, 'tests': results, 'fixture': str(fixture),
              'source_sha256': {p.relative_to(project).as_posix(): hashlib.sha256(p.read_bytes()).hexdigest()
                                for p in sorted(inputs)},
              'network': 'ephemeral loopback fixture servers only', 'third_party_jars': []}
    (output / 'results.json').write_text(json.dumps(record, indent=2), encoding='utf-8')
    return 0 if compiled['exit_code'] == 0 and results and all(r['exit_code'] == 0 for r in results) else 1


if __name__ == '__main__':
    raise SystemExit(main())
