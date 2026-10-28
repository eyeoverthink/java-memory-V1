"""Compile Exodus with only a JDK, then assert its actual BIOS console in QEMU.

Requires Python standard library, java/javac and QEMU. Pillow is optional for PNG
previews; assertions read guest VGA memory and do not depend on image libraries.
Use --image to boot an existing image without rewriting it. Output must be new.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
import queue
import re
import shutil
import subprocess
import sys
import threading
import time


class Qemu:
    def __init__(self, executable, image, output):
        self.output = output
        self.events = []
        self.messages = queue.Queue()
        self.request_id = 0
        self.command = [str(executable), '-accel', 'tcg', '-m', '16', '-drive',
            'file=' + str(image) + ',format=raw,if=ide,snapshot=on', '-boot', 'c',
            '-nic', 'none', '-display', 'none', '-serial', 'none', '-parallel', 'none',
            '-monitor', 'none', '-qmp', 'stdio', '-no-reboot', '-no-shutdown', '-S']
        self.process = subprocess.Popen(self.command, stdin=subprocess.PIPE,
            stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, encoding='utf-8',
            creationflags=getattr(subprocess, 'CREATE_NO_WINDOW', 0))
        threading.Thread(target=self.pump, daemon=True).start()
        try:
            self.greeting = self.receive()
            self.call('qmp_capabilities')
        except Exception:
            self.process.kill()
            self.process.wait(timeout=5)
            raise

    def pump(self):
        try:
            for line in self.process.stdout:
                self.messages.put(json.loads(line))
        except Exception as error:
            self.messages.put({'reader_error': str(error)})
        finally:
            self.messages.put({'eof': True})

    def receive(self):
        message = self.messages.get(timeout=10)
        if message.get('eof') or 'reader_error' in message:
            raise RuntimeError('QEMU monitor ended: ' + str(message))
        return message

    def call(self, name, arguments=None):
        self.request_id += 1
        request = {'execute': name, 'id': self.request_id}
        if arguments is not None:
            request['arguments'] = arguments
        self.process.stdin.write(json.dumps(request) + '\n')
        self.process.stdin.flush()
        while True:
            result = self.receive()
            if result.get('id') == self.request_id:
                if 'error' in result:
                    raise RuntimeError(str(result['error']))
                return result.get('return')
            self.events.append(result)

    def run(self):
        self.call('cont')

    def key(self, qcode):
        self.call('send-key', {'keys': [{'type': 'qcode', 'data': qcode}], 'hold-time': 20})
        time.sleep(0.06)

    def capture(self, label):
        # Capture while the guest is blocked on input, rather than inside an IRQ.
        for attempt in range(20):
            time.sleep(0.05)
            self.call('stop')
            registers = self.call('human-monitor-command', {'command-line': 'info registers'})
            if 'HLT=1' in registers or attempt == 19:
                break
            self.run()
        (self.output / (label + '-registers.txt')).write_text(registers, encoding='utf-8')
        for name, address, size in [('vga', 0xb8000, 4000), ('stage2', 0x10000, 1536),
                                     ('workspace', 0x20000, 16384)]:
            self.call('pmemsave', {'val': address, 'size': size,
                'filename': str(self.output / (label + '-' + name + '.bin'))})
        ppm = self.output / (label + '.ppm')
        self.call('screendump', {'filename': str(ppm)})
        preview = ppm
        try:
            from PIL import Image
            preview = ppm.with_suffix('.png')
            with Image.open(ppm) as screen:
                screen.save(preview)
        except ImportError:
            pass
        vga = (self.output / (label + '-vga.bin')).read_bytes()
        rows = [''.join(chr(c) if 32 <= c < 127 else ' '
                for c in vga[row*160:(row+1)*160:2]).rstrip() for row in range(25)]
        text = '\n'.join(rows).rstrip()
        (self.output / (label + '-screen.txt')).write_text(text, encoding='utf-8')
        stack = re.search(r'ESP=([0-9a-fA-F]+)', registers)
        return {'label': label, 'screen_text': text, 'screenshot': str(preview),
                'stack_pointer': stack.group(1) if stack else None, 'halted_on_input': 'HLT=1' in registers}

    def close(self):
        try:
            if self.process.poll() is None:
                self.call('quit')
                self.process.wait(timeout=8)
        finally:
            if self.process.poll() is None:
                self.process.kill()
                self.process.wait(timeout=5)
            (self.output / 'qmp-events.json').write_text(json.dumps(self.events, indent=2), encoding='utf-8')
        return {'exit_code': self.process.returncode, 'stderr': self.process.stderr.read()}


def find_qemu(explicit):
    for value in [explicit, os.environ.get('FRAYNIX_QEMU'), shutil.which('qemu-system-i386'),
                  shutil.which('qemu-system-i386.exe'), r'C:\Program Files\qemu\qemu-system-i386.exe',
                  r'C:\Program Files (x86)\qemu\qemu-system-i386.exe', r'C:\qemu\qemu-system-i386.exe']:
        if value and Path(value).is_file():
            return Path(value).resolve()
    raise RuntimeError('QEMU not found; use --qemu or FRAYNIX_QEMU')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output', type=Path, required=True)
    modes = parser.add_mutually_exclusive_group()
    modes.add_argument('--image', type=Path)
    modes.add_argument('--existing-classes', type=Path)
    parser.add_argument('--qemu')
    args = parser.parse_args()
    output = args.output.resolve()
    if output.exists():
        parser.error('Output must be a new directory')
    output.mkdir(parents=True)
    project = Path(__file__).resolve().parents[1]
    report = {'passed': False, 'checks': [], 'snapshots': [], 'output': str(output)}
    emulator = None
    image = None
    original = None

    def check(name, condition):
        report['checks'].append({'name': name, 'passed': bool(condition)})
        if not condition:
            raise AssertionError(name)

    def run(name, command, cwd):
        with (output / (name + '.log')).open('wb') as log:
            result = subprocess.run(command, cwd=cwd, stdout=log, stderr=subprocess.STDOUT, timeout=120)
        check(name, result.returncode == 0)

    try:
        qemu_path = find_qemu(args.qemu)
        if args.image:
            image = args.image.resolve()
        else:
            java, javac = shutil.which('java'), shutil.which('javac')
            if not java or not javac:
                raise RuntimeError('java and javac are required on PATH')
            classes, fixture = output / 'classes', output / 'fixture'
            classes.mkdir()
            fixture.mkdir()
            sources = [project / 'src/test/java/fraymus/exodus/ExodusStage2KernelTest.java',
                       project / 'src/test/java/fraymus/runtime/ExodusInvocationHelper.java']
            classpath = str(classes)
            if args.existing_classes:
                classpath += os.pathsep + str(args.existing_classes.resolve())
            else:
                sources += [project / 'src/main/java/fraymus/exodus/ExodusStage2Kernel.java',
                            project / 'src/main/java/fraymus/os/FrayFSBuilder.java']
            report['source_sha256'] = {str(p): hashlib.sha256(p.read_bytes()).hexdigest() for p in sources}
            run('compile', [javac, '--release', '21', '-encoding', 'UTF-8', '-classpath', classpath,
                            '-d', str(classes), *map(str, sources)], fixture)
            run('integrity-tests', [java, '-cp', classpath, 'fraymus.exodus.ExodusStage2KernelTest',
                                   str(fixture)], fixture)
            run('shell-backend', [java, '-cp', classpath, 'fraymus.runtime.ExodusInvocationHelper'], fixture)
            shell_output = (output / 'shell-backend.log').read_text(encoding='utf-8')
            check('Shell backend reports canonical image', 'FRAYNIX STAGE2 KERNEL ONLINE' in shell_output
                  and 'EXODUS COMPLETE' in shell_output and 'LOAD FAILED' not in shell_output)
            image = fixture / 'fraynix_genesis.img'
        original = image.read_bytes()
        report['image'] = str(image)
        report['sha256_before'] = hashlib.sha256(original).hexdigest().upper()
        check('2048-byte image', len(original) == 2048)
        check('BIOS boot signature', original[510:512] == bytes.fromhex('55aa'))
        emulator = Qemu(qemu_path, image, output)
        report['command'] = emulator.command
        report['qemu_version'] = emulator.greeting
        emulator.run()
        time.sleep(1)
        startup = emulator.capture('startup')
        report['snapshots'].append(startup)
        check('Three sectors loaded intact', (output / 'startup-stage2.bin').read_bytes() == original[512:])
        check('Banner displayed', 'FRAYNIX STAGE2 KERNEL ONLINE' in startup['screen_text'])
        check('Handoff displayed', 'BIOS MBR LOADER -> SECOND-STAGE HANDOFF VERIFIED' in startup['screen_text'])
        check('Memory initialized', 'HDC OK (16384 BYTES INITIALIZED)' in startup['screen_text']
              and (output / 'startup-workspace.bin').read_bytes() == bytes(16384))
        check('Prompt displayed', startup['screen_text'].splitlines()[-1] == '>')
        check('Waiting on keyboard', startup['halted_on_input'])
        emulator.run()
        for key in ['t', 'e', 's', 't', 'ret']:
            emulator.key(key)
        entered = emulator.capture('after_enter')
        report['snapshots'].append(entered)
        check('Text echoed and Enter acknowledged', '> test\nOK\n>' in entered['screen_text'])
        emulator.run()
        for key in ['backspace', 'a', 'b', 'backspace', 'c', 'ret']:
            emulator.key(key)
        backspace = emulator.capture('after_backspace')
        report['snapshots'].append(backspace)
        check('Backspace preserves prompt and edits input', '> ac\nOK\n>' in backspace['screen_text'])
        emulator.run()
        for _ in range(8):
            emulator.key('ret')
        repeated = emulator.capture('repeated_input')
        report['snapshots'].append(repeated)
        check('Ten Enter cycles acknowledged', repeated['screen_text'].splitlines().count('OK') == 10)
        check('Prompt survives repeated input', repeated['screen_text'].splitlines()[-1] == '>')
        check('Keyboard stack does not leak', repeated['halted_on_input']
              and startup['stack_pointer'] is not None
              and repeated['stack_pointer'] == startup['stack_pointer'])
        check('Guest code remains intact after input', (output / 'repeated_input-stage2.bin').read_bytes() == original[512:])
        report['passed'] = True
    except Exception as error:
        report['error'] = str(error)
    finally:
        if emulator is not None:
            try:
                report['qemu_exit'] = emulator.close()
            except Exception as error:
                report['passed'] = False
                report['cleanup_error'] = str(error)
        if image is not None and original is not None:
            report['sha256_after'] = hashlib.sha256(image.read_bytes()).hexdigest().upper()
            unchanged = image.read_bytes() == original
            report['checks'].append({'name': 'Host image unchanged by emulator', 'passed': unchanged})
            report['passed'] = report['passed'] and unchanged
        (output / 'report.json').write_text(json.dumps(report, indent=2), encoding='utf-8')
    print(json.dumps({'passed': report['passed'], 'checks': report['checks'],
                      'error': report.get('error'), 'report': str(output / 'report.json')}, indent=2))
    return 0 if report['passed'] else 1


if __name__ == '__main__':
    sys.exit(main())
