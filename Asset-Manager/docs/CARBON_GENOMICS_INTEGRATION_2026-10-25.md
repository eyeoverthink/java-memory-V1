# Carbon-3B genomics integration - October 25, 2026

Fraynix - By Vaughn Scott

## Installed and executed

Carbon-3B is downloaded locally and exposed through the existing Java Fraynix
shell, its AI tool loop, and the system command executor. It loads on demand.
The Java application remains the control plane; model inference runs in a local
Python worker using PyTorch and Transformers. Runtime inference is offline and
user FASTA sequences are not sent to a model service.

Publisher: https://huggingface.co/HuggingFaceBio/Carbon-3B
License: Apache-2.0
Pinned revision: `07c88a21cbb8a18a442baf651d896aba6057979d`

Installed snapshot:
`F:\Fraynix-Models\HuggingFaceBio\Carbon-3B\07c88a21cbb8a18a442baf651d896aba6057979d`

All three weight shards passed their publisher SHA-256 checks. The local
`installation.json` records the revision, license, source and file hashes.
The selected download is approximately 6.9 GB. Weights are outside the source
workspace.

Runtime config: `C:\Users\eyeka\.fraymus\models\carbon-runtime.json`
Current default: `C:\Python314\python.exe`, CPU float32, eight threads.
Installed inference packages: torch 2.11.0+cpu, transformers 5.20.2,
accelerate 1.12.0, huggingface_hub 2.3.0, safetensors 0.8.0.

An isolated CUDA runtime is also installed under
`F:\Fraynix-Models\runtimes\carbon-py314-cu128` with torch 2.11.0+cu128.
A simple CUDA matrix multiplication passed, but a full model run using automatic
GPU/CPU offload failed with `CUDA error: an illegal instruction was encountered`.
The cause is unresolved. GPU model inference is not validated. The default was
changed to the CPU path that passed actual scoring and generation. No other
application or running model was stopped to make room on the GPU.

## What the old FASTA files contain

Ten original FASTA files were found in the application root. Every record has
8192 A/C/G/T characters. Nine reproduce the current AeonApotheosis label-seeded
64-bit concept vector exactly. VAUGHN reproduces the legacy Java String.hashCode
seeded vector used in both AEON_Omega and AEON_Leviathan. These algorithm matches
establish encoding provenance; they do not identify which historical process
wrote a particular file.

The exporter maps two vector bits to one nucleotide symbol. It does not query a
biological reference, train a genomic model, or establish a concept's biological
meaning. These are valid DNA-alphabet sequences in FASTA format. Their names and
length do not establish genes, plasmid viability, or phenotypes. A functional
sequence could not be confirmed or ruled out from alphabet and length alone.
No reference alignment, biological annotation, or functional assay was performed.

Original inventory:
`reports/genomics/inventory-20261025T175334Z-755e34da.json`

All ten original file hashes were checked again after model generation and were
unchanged. Future `transcribe` exports retain the same sequence bytes but now
identify themselves as concept-vector encodings with biological function
unvalidated. The former log claiming a bacteriophage/plasmid match from size
alone was corrected. Legacy filenames are retained for compatibility.

## DNA model protocol

The adapter implements the canonical DNA subset of the publisher's tokenizer:
non-overlapping six-mers in ATCG vocabulary order, with an explicit `<dna>`
opening token. Native context is 32768 tokens. The full hybrid tokenizer also
supports English and metadata; this adapter does not implement those paths.
The downloaded custom tokenizer is not executed, and its implicit Qwen model
download is avoided. Stock local Llama model code loads the safetensors with
`local_files_only=True` and `trust_remote_code=False`.

An 8192-base primer contains 8190 bases in complete six-mers. The final two bases
are explicitly reported as unscored; generation records their exclusion. Scores
are mean next-token log probabilities over DNA tokens using the full model
vocabulary, excluding the end tag. The transformer runs once and the vocabulary
projection is chunked to reduce peak memory use.

The optimized scorer agreed with the stock model forward pass on a test input:
absolute mean-log-probability difference `5.449567517246123e-7`. The same installed
model also produced a valid 24-base continuation in that numerical check.

PAIN and HATE were scored against composition-matched controls: the same 8190
bases shuffled with seed 42. Their score differences were approximately -0.00327
and +0.00381 per six-mer respectively. This small comparison is not evidence of
specific biological function. The complete original-file comparison is recorded
below.

## Actual new creation

A Java bridge invocation generated 120 new bases from the PAIN primer. It used
8190 existing primer bases and wrote a separate 8310-base FASTA. The exact primer
prefix, canonical alphabet, new-base count and file hash were verified.

FASTA:
`reports/genomics/carbon-continuation-2b9a4873858742ceb245d54a30972dd2.fasta`

Provenance:
`reports/genomics/carbon-generation-20261025T180532Z-e4dab590.json`

The report records the model revision, runtime, parent file and sequence hashes,
output hash, aligned primer length and decoding settings. Decoding is greedy and
restricted to canonical DNA six-mer tokens. This proves model-guided sequence
creation, not biological function.

## Where the knowledge and creation paths are wired

- `fraymus.genomics.CarbonGenomicsBridge` is a JDK-only process boundary with
  argument lists, quoted Windows paths, timeout handling and failure propagation.
- FraynixBoot exposes `genomics` and `carbon`, plus `apotheosis analyze` and
  `apotheosis generate`. The system executor recognizes the same specialist.
- DNA-related AI prompts receive the complete local knowledge pack and creation
  skill. Their tool loop can execute `[GENOMICS: <arguments>]`, feed the actual
  result back to the language model, and persist successful evidence.
- DNA-related code-generation and grounded-analysis prompts receive the same
  knowledge. Code can call `CarbonGenomicsBridge.execute(List<String>)` rather
  than invent a tokenizer or claim a model ran from generated text alone.
- Successful commands in the shell persist summaries in `GENOMICS_ANALYSIS`;
  `genomics learn` persists the full pack and skill in `GENOMICS_KNOWLEDGE`.
- The generated source pack also participates in the existing open-model
  absorption path. That older path stores short samples; the direct prompt
  integration supplies the full instructions.
- The SkillLoader Usage parser now preserves all instruction paragraphs across
  LF/CRLF, blank lines, subsequent sections and end of file.
- The root offline kernel receives the source pack, skill and measured reports
  through its existing persistent ingestion path.

This teaches retrieval and tool use and changes how the application can create
sequences. It does not fine-tune Carbon weights or silently turn the old vector
exporter into a genomic model.

## Commands

Save and exit any old Fraynix JVM before starting the updated classes. From
`F:\Full-AI-System\Java-Memory\Asset-Manager`:

```powershell
java -cp "build/classes/java/main;build/resources/main" fraymus.FraynixBoot
```

Then:

```text
genomics status
genomics learn
genomics inventory
genomics analyze PAIN_Plasmid.fasta HATE_Plasmid.fasta --compare-shuffled
genomics generate PAIN_Plasmid.fasta --bases 120
ai genomics status
```

Without starting another full Fraynix runtime against live memory:

```powershell
java -cp "build/classes/java/main;build/resources/main" fraymus.genomics.CarbonGenomicsBridge status
java -cp "build/classes/java/main;build/resources/main" fraymus.genomics.CarbonGenomicsBridge analyze PAIN_Plasmid.fasta --compare-shuffled
```

Reports and worker logs are under `reports/genomics/`. Each generated FASTA and
report uses a unique name. The existing originals are read as primers.

## Verification and evidence

- Full application and included module source compilation: passed offline.
- Seven DNA codec/FASTA/report regressions: passed, including all 4096 six-mers.
- Fresh JDK-only Carbon bridge checks: passed; real worker status and error
  propagation tested without loading the model.
- Actual AI-path test with a deterministic local fixture language model: the
  complete creation skill reached the AI prompt; `[GENOMICS: learn]` executed,
  the worker result returned to the AI, and one knowledge block persisted.
  This tests wiring; the fixture response is not a model intelligence benchmark.
- Isolated Fraynix shell status/inventory/learn/shutdown: passed. A separate
  process restored its knowledge and analysis blocks with journal integrity.
- Real installed model likelihood and generation checks: passed on CPU.
- Legacy PAIN vector hash and length unchanged after exporter label correction.
- The org.json startup crash was also reproduced and fixed; see
  `OLLAMA_JSON_STARTUP_FIX_2026-10-25.md` for its 83 protocol checks, 72 JSON
  checks, existing model tests and classes-only boot proof.

Detailed tool logs and before/after source evidence:
`C:\Users\eyeka\.codex\validation\ollama-json-fix-20261025-futigems`

## Next measured improvements

Evaluate a local reference corpus with known provenance and held-out controls.
Retain a training/evaluation split before considering weight updates. Extend
annotation only when actual reference evidence is available, and benchmark GPU
inference independently before changing the working CPU default. New successful
analyses and creations should retain parent hashes, model revision, settings,
measured scores and validation state in memory.

## Complete original-file model comparison

Report: `reports/genomics/carbon-analysis-20261025T181902Z-50a13b6c.json`

| Original FASTA | GC % | Exact encoder match | Mean logp / 6-mer | Delta vs shuffle |
| :--- | ---: | :--- | ---: | ---: |
| [NEW_CONCEPT]_Plasmid.fasta | 48.94 | current | -8.369541 | -0.010953 |
| CONSCIOUSNESS_Plasmid.fasta | 49.41 | current | -8.356168 | -0.001224 |
| DNA_Plasmid.fasta | 48.77 | current | -8.358408 | +0.002179 |
| HARMONY_Plasmid.fasta | 49.58 | current | -8.367914 | -0.008661 |
| HATE_Plasmid.fasta | 50.93 | current | -8.357517 | +0.003808 |
| LOVE_Plasmid.fasta | 50.59 | current | -8.363907 | -0.011061 |
| PAIN_Plasmid.fasta | 49.69 | current | -8.360136 | -0.003266 |
| STATUS_Plasmid.fasta | 49.73 | current | -8.358273 | +0.005005 |
| SYNTHESIS_1292827920966500_Plasmid.fasta | 50.18 | current | -8.353300 | +0.011050 |
| VAUGHN_Plasmid.fasta | 50.07 | legacy hash | -8.367084 | -0.013837 |

All rows scored 8190 bases and reported two unscored trailing bases. One
composition-matched shuffled control was used per row. Four deltas were positive
and six negative. These small differences do not provide a functional annotation.
The full CPU batch took 649.64 seconds.
