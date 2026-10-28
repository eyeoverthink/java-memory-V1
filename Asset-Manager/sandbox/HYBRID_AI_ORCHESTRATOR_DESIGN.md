# Modular Hybrid AI Orchestrator - Design Document

**Status**: Sandbox Design Phase  
**Date**: April 11, 2026  
**Purpose**: Organic, adaptive multi-backend AI routing system

---

## Executive Summary

The Modular Hybrid AI Orchestrator is an isolated sandbox system designed to intelligently route AI queries across multiple backends (openclaw, neoClaw, obsidian, ollama, gemma3, claude) based on task requirements, model capabilities, and evolutionary learning. The system is "organic" - it adapts and evolves based on usage patterns and performance metrics.

---

## Available Infrastructure

### AI Backends

1. **openclaw** - Custom TypeScript/JavaScript agent system (5263+ files)
   - Location: `openclaw-main/openclaw-main/`
   - Capabilities: Agent orchestration, skills, extensions
   - Interface: TypeScript/JavaScript

2. **neoClaw** - Enhanced openclaw (assumed evolution)
   - Capabilities: Advanced agent operations
   - Status: To be investigated

3. **obsidian** - Knowledge management system
   - Location: `obsidian/eyeoverthink/`
   - Capabilities: Structured knowledge retrieval
   - Interface: Markdown-based

4. **ollama** - Local LLM infrastructure
   - Location: `ollama-models/`
   - Models: eyeoverthink, fraymus-genesis, fraymus-nexus, fraymus-oracle
   - Capabilities: Local inference, custom models
   - Interface: HTTP API

5. **gemma3** - Google's LLM
   - Capabilities: Creative generation, reasoning
   - Interface: To be determined (likely via API)

6. **offline claude** - Anthropic's model (offline)
   - Capabilities: Chain-of-thought reasoning, analysis
   - Interface: To be determined

---

## Organic Architecture

### Core Principles

1. **Evolutionary Routing** - Routes evolve based on success/failure patterns
2. **Adaptive Selection** - Backend selection adapts to query characteristics
3. **Self-Healing** - Fallback chains automatically adjust when backends fail
4. **Performance Learning** - System learns which backends excel at which tasks
5. **Organic Growth** - New backends can be added without reconfiguration

### Architecture Layers

```
┌─────────────────────────────────────────────────────────────────┐
│                  ORGANIC ORCHESTRATOR CORE                       │
├─────────────────────────────────────────────────────────────────┤
│  1. Query Analyzer (Task Classification)                       │
│     ├─ Intent detection                                        │
│     ├─ Complexity assessment                                   │
│     ├─ Latency requirements                                     │
│     └─ Accuracy needs                                          │
├─────────────────────────────────────────────────────────────────┤
│  2. Evolutionary Router (Adaptive Backend Selection)           │
│     ├─ Performance history tracking                             │
│     ├─ Success rate metrics                                    │
│     ├─ Latency profiling                                       │
│     └─ Cost analysis                                           │
├─────────────────────────────────────────────────────────────────┤
│  3. Backend Registry (Dynamic Backend Management)              │
│     ├─ Backend capabilities                                     │
│     ├─ Health monitoring                                        │
│     ├─ Load balancing                                          │
│     └─ Fallback chains                                          │
├─────────────────────────────────────────────────────────────────┤
│  4. Result Synthesizer (Multi-Backend Aggregation)              │
│     ├─ Ensemble combination                                     │
│     ├─ Cross-validation                                         │
│     ├─ Confidence scoring                                       │
│     └─ Consensus building                                      │
├─────────────────────────────────────────────────────────────────┤
│  5. Evolution Engine (Self-Improvement)                        │
│     ├─ Pattern recognition                                      │
│     ├─ Route optimization                                       │
│     ├─ Backend discovery                                        │
│     └─ Adaptive learning                                       │
└─────────────────────────────────────────────────────────────────┘
```

---

## Backend Capabilities Matrix

| Backend | Reasoning | Generation | Knowledge | Speed | Local | Creative |
|---------|-----------|------------|-----------|-------|-------|----------|
| openclaw | Medium | High | Low | Fast | Yes | High |
| neoClaw | High | High | Medium | Fast | Yes | High |
| obsidian | Low | Low | High | Fast | Yes | Low |
| ollama | High | High | Medium | Medium | Yes | Medium |
| gemma3 | High | High | Medium | Slow | No | High |
| claude | Very High | High | Medium | Slow | No | Medium |

---

## Routing Logic (Evolutionary)

### Task Types

1. **Reasoning Tasks** (why, how, explain, analyze)
   - Primary: claude (best chain-of-thought)
   - Secondary: ollama (local speed)
   - Tertiary: neoClaw (custom reasoning)

2. **Generative Tasks** (create, generate, compose)
   - Primary: gemma3 (creative output)
   - Secondary: openclaw (custom generation)
   - Tertiary: ollama (local generation)

3. **Knowledge Retrieval** (facts, information, lookup)
   - Primary: obsidian (structured knowledge)
   - Secondary: ollama (semantic search)
   - Tertiary: neoClaw (knowledge agents)

4. **Fast Inference** (real-time, low latency)
   - Primary: ollama (local speed)
   - Secondary: openclaw (agent speed)
   - Tertiary: neoClaw (optimized)

5. **Complex Analysis** (multi-step, deep reasoning)
   - Primary: claude (deep reasoning)
   - Secondary: gemma3 (analysis)
   - Tertiary: ollama (local analysis)

### Evolutionary Adaptation

The system tracks:
- Success rate per backend per task type
- Average latency per backend
- Resource utilization
- Error rates
- User satisfaction

Routes evolve based on:
- Performance degradation → switch to better backend
- Latency spikes → activate faster backend
- Backend failure → trigger fallback chain
- New patterns → discover optimal routes

---

## Sandbox Implementation Plan

### Phase 1: Core Infrastructure (Isolated)

1. **Backend Interface Layer**
   - Define unified interface for all backends
   - Implement adapters for each backend
   - Health monitoring system

2. **Query Analyzer**
   - Task classification
   - Complexity scoring
   - Requirement extraction

3. **Basic Router**
   - Static routing rules
   - Simple fallback chains
   - Performance tracking

### Phase 2: Evolutionary Features

4. **Performance Tracking**
   - Success rate metrics
   - Latency profiling
   - Resource monitoring

5. **Evolution Engine**
   - Pattern recognition
   - Route optimization
   - Adaptive learning

6. **Result Synthesizer**
   - Ensemble combination
   - Cross-validation
   - Consensus building

### Phase 3: Advanced Features

7. **Backend Discovery**
   - Automatic backend detection
   - Capability assessment
   - Dynamic registration

8. **Self-Healing**
   - Automatic failure recovery
   - Circuit breaker patterns
   - Health-based routing

9. **Organic Growth**
   - Plugin architecture
   - Hot-swappable backends
   - Runtime reconfiguration

---

## Safety & Isolation

### Sandbox Guarantees

1. **No System Modification** - Zero changes to working AGI system
2. **Isolated Execution** - Separate process/memory space
3. **No Persistent State** - All state in sandbox only
4. **No Network Exposure** - Local testing only
5. **No Data Loss** - Read-only access to existing data
6. **Reversible** - Can delete entire sandbox without impact

### Testing Strategy

1. **Unit Tests** - Each component in isolation
2. **Integration Tests** - Component interactions
3. **Evolution Tests** - Routing adaptation over time
4. **Failure Tests** - Backend failure scenarios
5. **Performance Tests** - Latency and throughput
6. **Security Tests** - Isolation guarantees

---

## Integration Path (After Sandbox Validation)

### Prerequisites

- All tests passing
- Performance metrics acceptable
- Evolutionary behavior validated
- Security guarantees verified

### Integration Steps

1. **Create Integration Branch** - Separate from main development
2. **Add to SentientCore** - Wire/unwire pattern
3. **Update LLM Integration** - Replace single backend with orchestrator
4. **Update Generative Engine** - Use orchestrator for generation
5. **Update Meta-Learning** - Use orchestrator for analysis
6. **Comprehensive Testing** - Full system integration tests
7. **Performance Validation** - No regression
8. **Rollback Plan** - Ability to revert if needed

---

## Success Criteria

### Sandbox Validation

- ✅ All backends accessible and functional
- ✅ Routing logic correctly classifies tasks
- ✅ Evolutionary adaptation works as expected
- ✅ Fallback chains trigger appropriately
- ✅ Performance tracking accurate
- ✅ Zero impact on working system
- ✅ All tests passing (unit + integration)

### Integration Readiness

- ✅ Wire/unwire pattern implemented
- ✅ Full backward compatibility
- ✅ No performance regression
- ✅ All existing tests still passing
- ✅ New orchestrator tests passing
- ✅ Documentation complete

---

## Next Steps

1. **Create sandbox structure**
2. **Implement backend interface layer**
3. **Implement query analyzer**
4. **Implement basic router**
5. **Create comprehensive tests**
6. **Validate in isolation**
7. **Document integration path**
8. **Await approval for integration**

---

**Document Version**: 1.0  
**Status**: Design Complete - Ready for Implementation
