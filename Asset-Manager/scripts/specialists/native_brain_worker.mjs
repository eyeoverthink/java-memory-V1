import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import { pathToFileURL } from 'node:url';

const sha = bytes => crypto.createHash('sha256').update(bytes).digest('hex');
const [, , rootArgument, requestArgument, stateArgument, outputArgument] = process.argv;
try {
  const root = path.resolve(rootArgument);
  const requestBytes = fs.readFileSync(requestArgument);
  const request = JSON.parse(requestBytes);
  const brain = await import(pathToFileURL(path.join(root, 'brain/index.js')));
  const outputDir = path.resolve(outputArgument);
  fs.mkdirSync(outputDir, { recursive: true });
  let result;
  const methods = {
    ledgerFromFloats: ['phi75.js', 'ledgerFromFloats'], ledgerWithOutliers: ['phi75.js', 'ledgerWithOutliers'],
    createPhi75Neuron: ['phi75.js', 'createPhi75Neuron'], trainPhi75: ['phi75.js', 'trainPhi75'],
    blocksFromWeights: ['layerTrain.js', 'blocksFromWeights'], reconstructBlocks: ['layerTrain.js', 'reconstructBlocks'],
    fitRowGains: ['layerTrain.js', 'fitRowGains'], trainLedgerMatrix: ['layerTrain.js', 'trainLedgerMatrix'],
    createInitialNcmState: ['sovereignEcosystem.js', 'createInitialNcmState'], classifyAndUpdateNcm: ['sovereignEcosystem.js', 'classifyAndUpdateNcm'],
    ensureMernProject: ['sovereignEcosystem.js', 'ensureMernProject'],
    computeInteroceptionAndAffect: ['emotionCore.js', 'computeInteroceptionAndAffect'], appraiseSituation: ['emotionCore.js', 'appraiseSituation'],
    computeBehavioralDials: ['emotionCore.js', 'computeBehavioralDials'], runLocalNeedleReflex: ['emotionCore.js', 'runLocalNeedleReflex'],
    bytesToDna: ['emojiCodec.js', 'bytesToDna'], dnaToBytes: ['emojiCodec.js', 'dnaToBytes'],
    computeGenomeParity: ['autopoieticAgent.js', 'computeGenomeParity'], verifyGenomeParity: ['autopoieticAgent.js', 'verifyGenomeParity'],
    healGenomeBytes: ['autopoieticAgent.js', 'healGenomeBytes'], computeRadius: ['orbital.js', 'computeRadius'],
    computeContinuity: ['orbital.js', 'computeContinuity']
  };
  if (request.operation === 'think') {
    const state = fs.existsSync(stateArgument) ? JSON.parse(fs.readFileSync(stateArgument, 'utf8')) : brain.createMind();
    const mind = brain.normalizeMind(state);
    const goal = String(request.goal ?? '');
    const recalled = /^recall\s+([A-Za-z][A-Za-z0-9_]*)\s*$/i.exec(goal);
    // The historical implementation can return inherited Object members as facts.
    if (recalled && !Object.hasOwn(mind.facts, recalled[1].toLowerCase())) {
      result = { mind, verdict: 'FAIL', steps: [{ phase: 'reject', detail: 'No own stored fact matches this key.' }] };
    } else {
      result = brain.think(mind, goal);
    }
    if (result.verdict === 'PASS' || result.verdict === 'REWRITTEN') {
      const temporary = stateArgument + '.' + crypto.randomUUID() + '.tmp';
      fs.mkdirSync(path.dirname(path.resolve(stateArgument)), { recursive: true });
      fs.writeFileSync(temporary, JSON.stringify(result.mind));
      fs.renameSync(temporary, stateArgument);
    }
    result = { ...result, state_path: path.resolve(stateArgument), state_sha256: fs.existsSync(stateArgument) ? sha(fs.readFileSync(stateArgument)) : null };
  } else if (request.operation === 'vm') {
    const code = brain.assemble(String(request.source));
    const interpreted = brain.runProgram(code);
    const compiled = brain.compileToWasm(code);
    const bytes = compiled?.wasm || compiled?.bytes || compiled;
    if (!WebAssembly.validate(bytes)) throw new Error('Generated VM module failed static validation');
    const { instance } = await WebAssembly.instantiate(bytes, {});
    const native = instance.exports.main();
    if (native !== interpreted.top) throw new Error('Interpreter and WASM disagree');
    const output = path.join(outputDir, 'frayvm-' + crypto.randomUUID() + '.wasm');
    fs.writeFileSync(output, bytes);
    result = { interpreted, wasm_result: native, interpreter_wasm_agree: true, output, output_sha256: sha(bytes),
      certificate_scope: 'interpreter/WASM result comparison and SHA256 artifact digest; no cryptographic PCC signature claim' };
  } else if (request.operation === 'methods') {
    result = { methods: Object.keys(methods).sort(), documentation: 'data/specialists/native-methods.json' };
  } else if (request.operation === 'method') {
    if (!Object.hasOwn(methods, request.method) || !Array.isArray(request.args)) throw new Error('Choose a whitelisted native method and explicit args array');
    const [filename, exported] = methods[request.method];
    const module = await import(pathToFileURL(path.join(root, 'brain/kernel', filename)));
    const args = request.args;
    if (['bytesToDna', 'computeGenomeParity', 'verifyGenomeParity', 'healGenomeBytes'].includes(request.method)) {
      if (!Array.isArray(args[0]) || args[0].some(n => !Number.isInteger(n) || n < 0 || n > 255)) throw new Error('Byte arguments must be integers from 0 through 255');
      args[0] = Uint8Array.from(args[0]);
    }
    if (request.method === 'dnaToBytes' && (typeof args[0] !== 'string' || args[0].length % 4 || /[^ACGT]/.test(args[0]))) throw new Error('Digital DNA must contain complete four-base bytes');
    result = { method: request.method, value: await module[exported](...args),
      scope: 'native owner algorithm on supplied arguments; no external provider model or deployed model checkpoint is updated' };
  } else if (request.operation === 'gguf-inspect') {
    const gguf = await import(pathToFileURL(path.join(root, 'brain/kernel/gguf.js')));
    const model = await gguf.readGguf(String(request.path), { metadata: true });
    try { result = { version: model.version, metadata: model.metadata, tensors: model.tensors, read_only: true }; }
    finally { await gguf.closeGguf(model); }
  } else {
    throw new Error('operation must be think, vm, methods, method or gguf-inspect');
  }
  process.stdout.write(JSON.stringify({ ok: true, backend: 'fraymus-owner-brain', operation: request.operation,
    result, request_sha256: sha(requestBytes), source_manifest_sha256: sha(fs.readFileSync(path.join(root, 'source-intake.json'))),
    external_tools_executed: false, deployed_model_weights_updated: false }, (_, value) =>
      typeof value === 'bigint' ? value.toString() : ArrayBuffer.isView(value) ? Array.from(value) : value));
} catch (error) {
  process.stdout.write(JSON.stringify({ ok: false, error: error.message }));
  process.exitCode = 1;
}
