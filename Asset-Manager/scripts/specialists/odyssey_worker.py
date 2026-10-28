"""Official Odyssey SDK bridge. Hosted predictions never become measured robot feedback."""
import argparse
import asyncio
import dataclasses
import enum
import hashlib
import importlib.metadata
import json
import math
import os
from pathlib import Path
import re
import subprocess
import sys
import time

REVISION = '6d5f4e902251ddd15bac33319800e6a5321eaa5a'


def plain(value):
    if dataclasses.is_dataclass(value): return plain(dataclasses.asdict(value))
    if isinstance(value, enum.Enum): return value.value
    if isinstance(value, dict): return {k: plain(v) for k, v in value.items()}
    if isinstance(value, (list, tuple)): return [plain(v) for v in value]
    return value


def save(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    temp = path.with_suffix('.tmp')
    temp.write_text(json.dumps(plain(value), indent=2, allow_nan=False), encoding='utf-8')
    temp.replace(path)


def sha(path):
    with path.open('rb') as f: return hashlib.file_digest(f, 'sha256').hexdigest()


def number(value, name, minimum, maximum):
    if isinstance(value, bool) or not isinstance(value, (int, float)) or not math.isfinite(value) or not minimum <= value <= maximum:
        raise ValueError(f'{name} must be finite and in [{minimum}, {maximum}]')
    return value


def prepare(request, config):
    request = dict(request)
    provenance = {}
    prompt = request.get('prompt', '')
    if not isinstance(prompt, str): raise ValueError('prompt must be text')
    if request.get('scene_json'):
        path = Path(request['scene_json']).resolve()
        scene = json.loads(path.read_text(encoding='utf-8-sig'))
        provenance['scene'] = {'path': str(path), 'sha256': sha(path), 'conditioning': 'serialized scene text'}
        prompt += '\nFraynix scene snapshot (simulation state, not a camera observation):\n' + json.dumps(scene, separators=(',', ':'))
    if request.get('image'):
        from PIL import Image
        path = Path(request['image']).resolve()
        with Image.open(path) as image: image.verify()
        request['image'] = str(path)
        provenance['image'] = {'path': str(path), 'sha256': sha(path), 'conditioning': 'image'}
    request['prompt'] = prompt
    if request.get('operation') in ('simulate', 'stream', 'view'):
        if request.get('operation') != 'view' and not prompt.strip() and not request.get('image'): raise ValueError('A prompt, scene or image is required')
        duration = number(request.get('duration_seconds', 10), 'duration_seconds', .1, config.get('max_duration_seconds', 300))
        request['duration_seconds'] = duration
        interactions = request.get('interactions', [])
        previous = 0
        for item in interactions:
            at = number(item['at_seconds'], 'at_seconds', previous, duration)
            if not isinstance(item.get('prompt'), str) or not item['prompt'].strip(): raise ValueError('Interaction prompt is required')
            previous = at
        request['interactions'] = interactions
        if request.get('operation') == 'view':
            if interactions: raise ValueError('Passive viewers cannot send interactions')
            if not str(request.get('webrtc_url', '')).startswith('https://'): raise ValueError('A provider HTTPS webrtc_url is required')
    return request, provenance


async def stream(client, request, directory, config, spectator_factory=None):
    from PIL import Image
    frames = directory / 'frames'; frames.mkdir(exist_ok=True)
    timestamps, errors = [], []
    fps = number(request.get('capture_fps', 8), 'capture_fps', 1, 60)
    last = [-float('inf')]
    def frame_received(frame):
        try:
            stamp = float(frame.timestamp_ms)
            if stamp < last[0] + 1000 / fps: return
            Image.fromarray(frame.data).save(frames / f'{len(timestamps):06d}.png')
            timestamps.append(stamp); last[0] = stamp
        except Exception as error: errors.append(str(error))
    broadcast = {}
    def broadcast_ready(info):
        # A spectator token grants access. Keep it outside the evidence-memory payload.
        path = directory / 'broadcast-access.private.json'
        save(path, plain(info))
        broadcast.update({'private_access_file': str(path.resolve()), 'enabled': True})
    if request.get('operation') == 'view':
        if spectator_factory is None:
            from odyssey import connect_to_stream
            spectator_factory = connect_to_stream
        viewer = await spectator_factory(webrtc_url=request['webrtc_url'],
            spectator_token=os.environ[config.get('spectator_token_env', 'ODYSSEY_SPECTATOR_TOKEN')], on_video_frame=frame_received)
        stream_id = request.get('stream_id', 'passive-view')
        try: await asyncio.sleep(request['duration_seconds'])
        finally:
            await viewer.disconnect()
            save(directory / 'frames.json', {'timestamp_ms': timestamps, 'errors': errors, 'stream_id': stream_id})
    else:
        await client.connect(on_video_frame=frame_received, on_broadcast_ready=broadcast_ready,
                             on_stream_error=lambda *args: errors.append(' '.join(map(str, args))))
        stream_id = await client.start_stream(prompt=request['prompt'], portrait=request.get('portrait', False),
                                              image=request.get('image'), broadcast=request.get('broadcast', False))
        save(directory / 'stream.json', {'stream_id': stream_id, 'state': 'started'})
        start = time.monotonic()
        try:
            for item in request['interactions']:
                await asyncio.sleep(max(0, item['at_seconds'] - (time.monotonic() - start)))
                await client.interact(item['prompt'])
            await asyncio.sleep(max(0, request['duration_seconds'] - (time.monotonic() - start)))
        finally:
            await client.end_stream()
            save(directory / 'frames.json', {'timestamp_ms': timestamps, 'errors': errors, 'stream_id': stream_id})
    if errors: raise RuntimeError('Stream reported errors: ' + '; '.join(errors))
    if len(timestamps) < 2: raise RuntimeError('No usable video received; stream identity saved for recording lookup')
    actual_fps = (len(timestamps) - 1) * 1000 / (timestamps[-1] - timestamps[0])
    output = directory / 'video.mp4'
    command = [config['ffmpeg'], '-y', '-v', 'error', '-framerate', str(actual_fps), '-i', str(frames / '%06d.png'),
               '-c:v', 'libx264', '-pix_fmt', 'yuv420p', '-movflags', '+faststart', str(output)]
    subprocess.run(command, check=True, capture_output=True, timeout=120)
    probe = subprocess.run([config['ffprobe'], '-v', 'error', '-count_frames', '-show_streams', '-of', 'json', str(output)],
                           check=True, capture_output=True, text=True, timeout=30)
    video = next(x for x in json.loads(probe.stdout)['streams'] if x.get('codec_type') == 'video')
    if int(video.get('nb_read_frames', 0)) != len(timestamps): raise RuntimeError('Encoded video frame count differs')
    return {'stream_id': stream_id, 'artifact': str(output.resolve()), 'sha256': sha(output),
            'frames': len(timestamps), 'frame_timestamps': str((directory / 'frames.json').resolve()),
            'width': video['width'], 'height': video['height'], 'recording_operation': 'recording',
            'passive_viewer': request.get('operation') == 'view', 'broadcast': broadcast}


async def execute(request, config, root, client_factory=None, spectator_factory=None):
    allowed = {'operation', 'request_id', 'prompt', 'duration_seconds', 'interactions', 'scene_json', 'image',
               'portrait', 'capture_fps', 'broadcast', 'job_id', 'stream_id', 'webrtc_url', 'limit', 'offset'}
    unknown = set(request) - allowed
    if unknown: raise ValueError('Unsupported request fields (credentials belong in environment variables): ' + ', '.join(sorted(unknown)))
    for flag in ('portrait', 'broadcast'):
        if flag in request and not isinstance(request[flag], bool): raise ValueError(flag + ' must be a boolean')
    operation = request.get('operation', 'status')
    base = {'ok': True, 'backend': 'odyssey-hosted', 'operation': operation, 'sdk_revision': REVISION,
            'provider_model': 'provider-selected', 'provider_model_verified': False,
            'output_kind': 'generated prediction', 'robot_decoder_configured': False,
            'physical_hardware_executed': False, 'measured_feedback_applied': False}
    key = os.environ.get(config.get('api_key_env', 'ODYSSEY_API_KEY'), '')
    if operation == 'status':
        try: version = importlib.metadata.version('odyssey')
        except importlib.metadata.PackageNotFoundError: version = None
        return base | {'sdk_version': version, 'api_key_present': bool(key), 'network_enabled': config.get('allow_network') is True,
                       'inference_executed': False, 'operations': ['simulate', 'poll', 'cancel', 'list', 'recording', 'stream', 'view']}
    if operation not in ('simulate', 'poll', 'cancel', 'list', 'recording', 'stream', 'view'): raise ValueError('Unknown Odyssey operation')
    if config.get('allow_network') is not True: raise ValueError('Hosted Odyssey is disabled: enable allow_network in odyssey-runtime.json')
    if operation == 'view':
        if not os.environ.get(config.get('spectator_token_env', 'ODYSSEY_SPECTATOR_TOKEN')): raise ValueError('Set the spectator token in the launching environment')
    elif not key: raise ValueError('Set ' + config.get('api_key_env', 'ODYSSEY_API_KEY') + ' in the launching environment')
    prepared, provenance = prepare(request, config)
    base['provenance'] = provenance
    directory = None
    if operation in ('simulate', 'stream', 'view'):
        identity = request.get('request_id', '')
        if not isinstance(identity, str) or not re.fullmatch(r'[A-Za-z0-9_-]{1,100}', identity): raise ValueError('A stable request_id is required for generation')
        directory = root / identity; directory.mkdir(parents=True, exist_ok=True)
        fingerprint = hashlib.sha256(json.dumps({'request': prepared, 'provenance': provenance}, sort_keys=True, allow_nan=False).encode()).hexdigest()
        claim = directory / 'submission.json'
        try:
            with claim.open('x', encoding='utf-8') as f: json.dump({'fingerprint': fingerprint, 'state': 'submission-in-progress'}, f)
        except FileExistsError:
            previous = json.loads(claim.read_text())
            if previous['fingerprint'] != fingerprint: raise ValueError('request_id already belongs to different input')
            result = directory / 'result.json'
            if result.is_file():
                saved = json.loads(result.read_text())
                artifact = saved.get('provider_result', {}).get('artifact')
                if artifact:
                    artifact = directory / 'video.mp4'
                    if not artifact.is_file() or sha(artifact) != saved['provider_result']['sha256']:
                        raise RuntimeError('Saved video is missing or modified; provider stream identity remains in the receipt')
                    saved['provider_result']['artifact'] = str(artifact.resolve())
                    saved['provider_result']['frame_timestamps'] = str((directory / 'frames.json').resolve())
                    if saved['provider_result'].get('broadcast', {}).get('private_access_file'):
                        saved['provider_result']['broadcast']['private_access_file'] = str((directory / 'broadcast-access.private.json').resolve())
                saved['receipt'] = str(result.resolve())
                saved['input_snapshot'] = str((directory / 'request.json').resolve())
                return saved | {'reused_receipt': True}
            raise RuntimeError('Submission outcome is uncertain; inspect saved receipt/list jobs before using a new request_id')
        save(directory / 'request.json', prepared)
    if client_factory is None:
        from odyssey import Odyssey
        client_factory = Odyssey
    client = None if operation == 'view' else client_factory(api_key=key, enable_telemetry=False)
    try:
        if operation == 'simulate':
            start = {'prompt': prepared['prompt']}
            if prepared.get('image'): start['image'] = prepared['image']
            script = [{'timestamp_ms': 0, 'start': start}]
            script += [{'timestamp_ms': round(x['at_seconds'] * 1000), 'interact': {'prompt': x['prompt']}} for x in prepared['interactions']]
            script.append({'timestamp_ms': round(prepared['duration_seconds'] * 1000), 'end': {}})
            response = plain(await client.simulate(script=script, portrait=prepared.get('portrait', False)))
        elif operation in ('stream', 'view'): response = await stream(client, prepared, directory, config, spectator_factory)
        elif operation == 'poll': response = plain(await client.get_simulate_status(request['job_id']))
        elif operation == 'cancel': response = plain(await client.cancel_simulation(request['job_id']))
        elif operation == 'recording': response = plain(await client.get_recording(request['stream_id']))
        else:
            limit = number(request.get('limit', 20), 'limit', 1, 100)
            response = plain(await client.list_simulations(limit=int(limit), offset=int(request.get('offset', 0))))
        result = base | {'provider_result': response, 'hosted_request_executed': True}
        if directory:
            result['receipt'] = str((directory / 'result.json').resolve())
            result['input_snapshot'] = str((directory / 'request.json').resolve())
            result['input_snapshot_sha256'] = sha(directory / 'request.json')
            save(directory / 'result.json', result)
        return result
    finally:
        if client is not None: await client.disconnect()


def main():
    parser = argparse.ArgumentParser(); parser.add_argument('--config', type=Path, required=True)
    parser.add_argument('--output-dir', type=Path, default=Path('reports/specialists/odyssey'))
    parser.add_argument('request', type=Path); args = parser.parse_args()
    config = json.loads(args.config.read_text(encoding='utf-8-sig'))
    model_root = os.environ.get('FRAYNIX_MODEL_ROOT', 'F:/Fraynix-Models')
    config = {k: v.replace('${MODEL_ROOT}', model_root) if isinstance(v, str) else v for k, v in config.items()}
    try:
        request = json.loads(args.request.read_text(encoding='utf-8-sig'))
        timeout = float(config.get('request_timeout_seconds', 480))
        result = asyncio.run(asyncio.wait_for(execute(request, config, args.output_dir), timeout=timeout))
    except Exception as error:
        key = os.environ.get(config.get('api_key_env', 'ODYSSEY_API_KEY'), '')
        message = str(error).replace(key, '[REDACTED]') if key else str(error)
        token = os.environ.get(config.get('spectator_token_env', 'ODYSSEY_SPECTATOR_TOKEN'), '')
        if token: message = message.replace(token, '[REDACTED]')
        print(json.dumps({'ok': False, 'backend': 'odyssey-hosted', 'error': message, 'physical_hardware_executed': False}))
        return 1
    print(json.dumps(plain(result), allow_nan=False)); return 0


if __name__ == '__main__': sys.exit(main())
