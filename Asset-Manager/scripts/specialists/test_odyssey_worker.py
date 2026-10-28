import asyncio
import importlib.util
import json
import os
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch

spec = importlib.util.spec_from_file_location('odyssey_worker', Path(__file__).with_name('odyssey_worker.py'))
worker = importlib.util.module_from_spec(spec); spec.loader.exec_module(worker)


class Client:
    calls = []
    def __init__(self, **kwargs): self.calls.append(('client', kwargs['enable_telemetry']))
    async def simulate(self, **kwargs): self.calls.append(('simulate', kwargs)); return {'job_id': 'provider-job-1', 'status': 'pending'}
    async def get_simulate_status(self, job): self.calls.append(('poll', job)); return {'job_id': job, 'status': 'completed'}
    async def cancel_simulation(self, job): self.calls.append(('cancel', job)); return {'job_id': job, 'status': 'cancelled'}
    async def list_simulations(self, **kwargs): return {'jobs': []}
    async def get_recording(self, stream): return {'stream_id': stream, 'video_url': 'https://example.invalid/recording'}
    async def disconnect(self): self.calls.append(('disconnect',))


class OdysseyTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(); self.root = Path(self.temp.name)
        self.env = patch.dict(os.environ, {'ODYSSEY_API_KEY': 'test-key'}); self.env.start()
        self.config = {'allow_network': True}; Client.calls = []
    def tearDown(self): self.env.stop(); self.temp.cleanup()
    def run_request(self, request, client=Client):
        return asyncio.run(worker.execute(request, self.config, self.root, client))
    def test_offline_status_never_constructs_network_client(self):
        self.config['allow_network'] = False
        self.assertFalse(self.run_request({'operation': 'status'})['inference_executed'])
        with self.assertRaisesRegex(ValueError, 'disabled'): self.run_request({'operation': 'list'})
        self.assertEqual(Client.calls, [])
    def test_missing_credentials_never_connects(self):
        with patch.dict(os.environ, {'ODYSSEY_API_KEY': ''}):
            with self.assertRaisesRegex(ValueError, 'Set ODYSSEY'): self.run_request({'operation': 'list'})
        self.assertEqual(Client.calls, [])
    def test_scene_provenance_script_and_durable_deduplication(self):
        scene = self.root / 'scene.json'; scene.write_text('{"objects":[{"id":"ball","x":3}]}')
        request = {'operation': 'simulate', 'request_id': 'one', 'prompt': 'Roll', 'scene_json': str(scene),
                   'duration_seconds': 5, 'interactions': [{'at_seconds': 2, 'prompt': 'Stop'}]}
        result = self.run_request(request)
        script = next(x[1]['script'] for x in Client.calls if x[0] == 'simulate')
        self.assertIn('"id":"ball"', script[0]['start']['prompt'])
        self.assertEqual(script[1]['timestamp_ms'], 2000)
        self.assertEqual(script[-1], {'timestamp_ms': 5000, 'end': {}})
        self.assertEqual(result['provenance']['scene']['sha256'], worker.sha(scene))
        self.assertFalse(result['provider_model_verified']); self.assertFalse(result['measured_feedback_applied'])
        self.assertTrue(self.run_request(request)['reused_receipt'])
        self.assertEqual(sum(x[0] == 'simulate' for x in Client.calls), 1)
        self.assertNotIn('test-key', (self.root / 'one/result.json').read_text())
        with self.assertRaisesRegex(ValueError, 'different input'): self.run_request(request | {'prompt': 'Changed'})
    def test_failed_submission_is_not_blindly_retried(self):
        class Broken(Client):
            async def simulate(self, **kwargs): raise RuntimeError('connection lost after send')
        request = {'operation': 'simulate', 'request_id': 'uncertain', 'prompt': 'Test'}
        with self.assertRaisesRegex(RuntimeError, 'connection lost'): self.run_request(request, Broken)
        self.assertEqual(Client.calls[-1], ('disconnect',))
        with self.assertRaisesRegex(RuntimeError, 'uncertain'): self.run_request(request)
    def test_resume_poll_cancel_recording_use_provider_identity(self):
        self.assertEqual(self.run_request({'operation': 'poll', 'job_id': 'old-job'})['provider_result']['job_id'], 'old-job')
        self.assertEqual(self.run_request({'operation': 'cancel', 'job_id': 'old-job'})['provider_result']['status'], 'cancelled')
        self.assertEqual(self.run_request({'operation': 'recording', 'stream_id': 'old-stream'})['provider_result']['stream_id'], 'old-stream')
        self.assertEqual(sum(x[0] == 'simulate' for x in Client.calls), 0)
    def test_invalid_duration_and_order_never_submit(self):
        request = {'operation': 'simulate', 'request_id': 'bad', 'prompt': 'test', 'duration_seconds': float('nan')}
        with self.assertRaises(ValueError): self.run_request(request)
        request['duration_seconds'] = 5
        request['interactions'] = [{'at_seconds': 3, 'prompt': 'A'}, {'at_seconds': 1, 'prompt': 'B'}]
        with self.assertRaises(ValueError): self.run_request(request)
        self.assertEqual(Client.calls, [])
    def test_unsupported_quality_or_inline_credentials_are_rejected(self):
        for field in ('quality', 'api_key'):
            with self.assertRaisesRegex(ValueError, 'Unsupported request fields'):
                self.run_request({'operation':'simulate','request_id':'invalid','prompt':'Test',field:'not-supported'})
        self.assertEqual(Client.calls, [])
    def test_stream_captures_and_verifies_actual_video(self):
        import numpy as np
        from types import SimpleNamespace
        class Streaming(Client):
            async def connect(self, **callbacks): self.callback = callbacks['on_video_frame']; self.broadcast_callback = callbacks['on_broadcast_ready']
            async def start_stream(self, **kwargs):
                if kwargs.get('broadcast'): self.broadcast_callback({'webrtc_url':'https://provider.invalid/whep', 'spectator_token':'private-token', 'hls_url':None})
                for stamp, color in [(0, 80), (125, 180)]:
                    self.callback(SimpleNamespace(timestamp_ms=stamp, data=np.full((16, 16, 3), color, dtype=np.uint8)))
                return 'stream-one'
            async def end_stream(self): self.calls.append(('end',))
            async def interact(self, prompt): self.calls.append(('interact', prompt))
        model_root = Path(os.environ.get('FRAYNIX_MODEL_ROOT', 'F:/Fraynix-Models'))
        self.config.update(ffmpeg=str(model_root / 'media/bin/ffmpeg.exe'), ffprobe=str(model_root / 'media/bin/ffprobe.exe'))
        result = self.run_request({'operation': 'stream', 'request_id': 'stream', 'prompt': 'Test', 'duration_seconds': .1, 'broadcast':True}, Streaming)
        video = result['provider_result']
        self.assertEqual(video['frames'], 2); self.assertEqual(video['sha256'], worker.sha(Path(video['artifact'])))
        self.assertEqual(Client.calls[-2:], [('end',), ('disconnect',)])
        self.assertNotIn('private-token', json.dumps(result))
        self.assertEqual(json.loads(Path(video['broadcast']['private_access_file']).read_text())['spectator_token'], 'private-token')
        relocated=self.root/'relocated';relocated.mkdir();(self.root/'stream').rename(relocated/'stream')
        again=asyncio.run(worker.execute({'operation':'stream','request_id':'stream','prompt':'Test','duration_seconds':.1,'broadcast':True},self.config,relocated,Streaming))
        self.assertTrue(again['reused_receipt']);self.assertTrue(Path(again['provider_result']['artifact']).is_relative_to(relocated))
    def test_passive_view_uses_spectator_connection_without_full_api_client(self):
        import numpy as np
        from types import SimpleNamespace
        disconnected = []
        class Viewer:
            async def disconnect(self): disconnected.append(True)
        async def connect(**kwargs):
            self.assertEqual(kwargs['spectator_token'], 'viewer-token')
            for stamp in (0,125): kwargs['on_video_frame'](SimpleNamespace(timestamp_ms=stamp, data=np.full((16,16,3),100,dtype=np.uint8)))
            return Viewer()
        def forbidden(**kwargs): self.fail('Passive viewing must not create a generating client')
        model_root = Path(os.environ.get('FRAYNIX_MODEL_ROOT','F:/Fraynix-Models'))
        self.config.update(ffmpeg=str(model_root/'media/bin/ffmpeg.exe'),ffprobe=str(model_root/'media/bin/ffprobe.exe'))
        with patch.dict(os.environ, {'ODYSSEY_API_KEY':'','ODYSSEY_SPECTATOR_TOKEN':'viewer-token'}):
            result=asyncio.run(worker.execute({'operation':'view','request_id':'viewer','webrtc_url':'https://provider.invalid/whep','duration_seconds':.1},self.config,self.root,forbidden,connect))
        self.assertTrue(result['provider_result']['passive_viewer']); self.assertEqual(disconnected,[True])
        self.assertNotIn('viewer-token',json.dumps(result))


if __name__ == '__main__': unittest.main()
