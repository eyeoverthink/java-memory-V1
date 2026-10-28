"""Standard-library, offline native Needle3 proposal worker. No functions are executed."""
import argparse
import ctypes
import hashlib
import json
import math
import os
from pathlib import Path
import sys
import time


def validate(value, schema, path='$'):
    kind = schema.get('type')
    checks = {'object': lambda v: isinstance(v, dict), 'array': lambda v: isinstance(v, list),
              'string': lambda v: isinstance(v, str), 'boolean': lambda v: isinstance(v, bool),
              'number': lambda v: type(v) in (int, float) and math.isfinite(v),
              'integer': lambda v: type(v) is int, 'null': lambda v: v is None}
    if kind not in checks or not checks[kind](value):
        raise ValueError(f'{path}: invalid or unsupported schema type {kind}')
    if 'enum' in schema and value not in schema['enum']:
        raise ValueError(f'{path}: value is outside enum')
    if 'const' in schema and value != schema['const']:
        raise ValueError(f'{path}: value differs from const')
    if kind == 'object':
        properties = schema.get('properties', {})
        if set(schema.get('required', [])) - value.keys():
            raise ValueError(f'{path}: missing required arguments')
        for key, child in value.items():
            if key not in properties:
                if schema.get('additionalProperties', False) is not True:
                    raise ValueError(f'{path}: unknown argument {key}')
            else:
                validate(child, properties[key], path + '.' + key)
    if kind == 'array':
        for index, child in enumerate(value):
            validate(child, schema.get('items', {}), f'{path}[{index}]')
    if kind in ('integer', 'number'):
        if value < schema.get('minimum', -math.inf) or value > schema.get('maximum', math.inf):
            raise ValueError(f'{path}: argument outside bounds')


def propose(config_path, request_path):
    config = json.loads(config_path.read_text(encoding='utf-8'))
    request = json.loads(request_path.read_text(encoding='utf-8'))
    goal, tools = request['goal'], request['tools']
    if not isinstance(goal, str) or not goal.strip() or not isinstance(tools, list) or not tools:
        raise ValueError('A goal and explicit shortlist of tool schemas are required')
    names = [tool['name'] for tool in tools]
    if len(set(names)) != len(names):
        raise ValueError('Duplicate tool names')
    for tool in tools:
        if not isinstance(tool['parameters'], dict) or tool['parameters'].get('type') != 'object':
            raise ValueError('Each tool needs an object parameter schema')
    for name in ('weights', 'library'):
        if hashlib.sha256(Path(config[name]).read_bytes()).hexdigest() != config[name + '_sha256']:
            raise ValueError(name + ' checksum failure')
    started = time.monotonic()
    engine = ctypes.CDLL(config['library'])
    engine.needle_load.argtypes = [ctypes.c_char_p, ctypes.c_uint64]
    engine.needle_load.restype = ctypes.c_int
    engine.needle_init.argtypes = [ctypes.c_char_p, ctypes.c_char_p, ctypes.c_char_p]
    engine.needle_init.restype = ctypes.c_int
    engine.needle_complete.argtypes = [ctypes.c_char_p, ctypes.POINTER(ctypes.c_float), ctypes.c_int,
                                      ctypes.c_int, ctypes.c_char_p, ctypes.c_int]
    engine.needle_complete.restype = ctypes.c_int
    weights = Path(config['weights']).read_bytes()
    if engine.needle_load(weights, len(weights)) < 0:
        raise RuntimeError('Native model loading failed')
    system = request.get('system', 'Select only explicitly supplied tools. Return proposals, never claim execution.')
    if engine.needle_init(system.encode(), json.dumps(tools).encode(), None) < 0:
        raise RuntimeError('Native schema initialization failed')
    output = ctypes.create_string_buffer(65536)
    limit = request.get('max_new_tokens', 512)
    if type(limit) is not int or not 1 <= limit <= 4096:
        raise ValueError('max_new_tokens must be an integer from 1 to 4096')
    if engine.needle_complete(goal.encode(), None, 0, limit, output, len(output)) < 0:
        raise RuntimeError('Native inference failed: ' + output.value.decode('utf-8', 'replace'))
    response = json.loads(output.value.decode())
    schemas = {tool['name']: tool['parameters'] for tool in tools}
    for call in response.get('function_calls') or []:
        if call.get('name') not in schemas:
            raise ValueError('Model proposed a tool outside the supplied shortlist')
        validate(call.get('arguments'), schemas[call['name']])
    return {'ok': True, 'model': config['model'], 'revision': config['revision'],
            'engine_version': config['engine_version'], 'result': response,
            'schema_validated': True, 'executed': False, 'seconds': round(time.monotonic() - started, 3),
            'network_used': False, 'weights_updated': False,
            'request_sha256': hashlib.sha256(request_path.read_bytes()).hexdigest(),
            'installation_sha256': hashlib.sha256(config_path.read_bytes()).hexdigest()}


if __name__ == '__main__':
    # Native stdout belongs in the diagnostic log; preserve a clean JSON protocol.
    protocol = os.fdopen(os.dup(sys.stdout.fileno()), 'w', encoding='utf-8')
    os.dup2(sys.stderr.fileno(), sys.stdout.fileno())
    parser = argparse.ArgumentParser()
    parser.add_argument('--config', type=Path, required=True)
    parser.add_argument('request', type=Path)
    args = parser.parse_args()
    try:
        result = propose(args.config, args.request)
    except Exception as error:
        print(json.dumps({'ok': False, 'error': str(error)}), file=protocol, flush=True)
        raise SystemExit(1)
    print(json.dumps(result), file=protocol, flush=True)
