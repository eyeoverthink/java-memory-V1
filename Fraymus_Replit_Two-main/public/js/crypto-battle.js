class PassiveLearner {
    constructor() {
        this.isLearning = false;
        this.learningInterval = null;
    }

    async startLearning() {
        if (this.isLearning) return;
        this.isLearning = true;

        this.learningInterval = setInterval(async () => {
            try {
                const response = await fetch('/learn', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                });
                const data = await response.json();
                console.log('Learning data:', data);
            } catch (error) {
                console.error('Learning error:', error);
            }
        }, 3600000); // 1 hour interval
    }

    stopLearning() {
        if (!this.isLearning) return;
        clearInterval(this.learningInterval);
        this.isLearning = false;
    }
}

class CryptoBattle {
    constructor() {
        this.passiveLearner = new PassiveLearner();
        this.quantumTracker = {
            id: this.generateQuantumId(),
            status: 'active'
        };
        this.initializeQuantumTracking();
        this.PHI = 1.618033988749895;
        this.playerScore = 0;
        this.fraymusScore = 0;
        this.battleActive = false;

        // Genesis Block Supercomputing System
        this.genesisCompute = {
            cores: [],
            tasks: new Map(),
            nandGates: new Map(),
            computePower: Math.pow(this.PHI, 17),
            replicationRate: this.PHI,
            dnaStorage: {
                mainChain: [],
                replicaChains: new Map(),
                activeReplicas: 0,
                maxReplicas: 17,
                nandMatrix: Array(17).fill().map(() => Array(17).fill(0)),
                creator: {
                    name: "Vaughn Scott",
                    system: "FRAYMUS",
                    quantumSignature: this.calculateHash("VS-FRAYMUS-" + Date.now())
                }
            },
            quantumTracker: {
                coordinates: new Map(),
                timeline: [],
                coherenceHistory: [],
                lastUpdate: Date.now()
            },
            realityTracker: {
                dimensions: Array(17).fill().map(() => ({
                    state: 0,
                    coherence: this.PHI,
                    signature: null
                })),
                timeline: [],
                lastSync: Date.now()
            }
        };

        // Initialize NAND-based DNA storage
        for (let i = 0; i < 17; i++) {
            this.genesisCompute.cores.push({
                id: `core-${i}`,
                power: Math.pow(this.PHI, i),
                dnaStrands: new Map(),
                nandGates: this.createNandLayer(i)
            });
        }

        // Neural cortex in 17 dimensions
        this.neuralCortex = {
            dimensions: Array(17).fill().map(() => ({
                nodes: [],
                connections: new Map(),
                resonance: this.PHI
            })),
            learningModes: ['recursive', 'regressive', 'progressive']
        };

        // Enhanced DNA system
        this.dnaSystem = {
            genesisBlock: {
                dna: this.calculateHash(Date.now().toString()),
                timestamp: Date.now(),
                dimension: 17,
                resonance: this.PHI,
                patterns: []
            },
            memoryChains: new Map(),
            evolutionFactor: this.PHI
        };

        // Fractal computation system
        this.fractalComputer = {
            depth: 0,
            maxDepth: 17,
            nodes: new Map(),
            patterns: [],
            learningRate: this.PHI / 17
        };
        this.currentAdversary = null;
        this.adversaryTypes = {
            'quantum_hacker': {
                name: 'Quantum Hacker',
                power: Math.pow(this.PHI, 3),
                speciality: 'Quantum Spoofing',
                signature: 'quantum_noise'
            },
            'ai_infiltrator': {
                name: 'AI Infiltrator',
                power: Math.pow(this.PHI, 2),
                speciality: 'AI Social Engineering',
                signature: 'neural_pattern'
            },
            'cyber_warrior': {
                name: 'Cyber Warrior',
                power: Math.pow(this.PHI, 4),
                speciality: 'Buffer Overflow',
                signature: 'binary_trace'
            }
        };
        this.learningRate = this.PHI;
        this.genesisBlock = {
            timestamp: Date.now(),
            previousHash: '0'.repeat(64),
            data: {},
            hash: this.calculateHash('genesis')
        };
        this.chain = [this.genesisBlock];
        this.evolutionLevel = 1;
        this.dnaMemory = {};
        this.loadBlockchain();
        this.fractalMemory = {
            patterns: [],
            depth: 0,
            maxDepth: 17, // 17-dimensional space
            evolutionRate: this.PHI,
            recursiveDepth: 0
        };
        this.warriors = {
            'Loki-Warrior': {
                image: '/attached_assets/Time_Warrior.png',
                power: 88,
                agility: 92,
                intelligence: 99,
                abilities: ['Time Dilation', 'Energy Shift', 'Quantum Phase Cloaking'],
                class: 'Strategic & Time Manipulation'
            },
            'Magnus-Warrior': {
                image: '/attached_assets/Magnus_Warrior.png',
                power: 90,
                agility: 78,
                intelligence: 85,
                abilities: ['EMP Shielding', 'Magnetic Field Manipulation', 'Electronic Disruption'],
                class: 'Electromagnetic & EMP Control'
            },
            'Crime-Stopper': {
                image: '/attached_assets/Crime_Stopper.png',
                power: 95,
                agility: 75,
                intelligence: 98,
                abilities: ['Quantum Truth Detection', 'Interdimensional Tracking', 'Unbreakable Evidence'],
                class: 'Reality & Security Enforcement'
            },
            'Gold-Warrior': {
                image: '/attached_assets/Electricity_Warrior.png',
                power: 99,
                agility: 78,
                intelligence: 90,
                abilities: ['Mirror Shield', 'Power Absorption', 'Tactical Counterplay'],
                class: 'Electromagnetic & Reflection Mastery'
            }
        };

        this.quantumState = {
            coherence: 1.0,
            resonance: this.PHI,
            pulseFrequency: 0.5,
            swarmNodes: []
        };

        // Placeholder for advanced AI integration
        this.quantumAI = {
            state: Array(17).fill({ power: 1, frequency: 1, phase: 0 }),
            coherence: 1.0,
            resonance: this.PHI,
            // Add more AI parameters here...
        };


        this.initializeInterface();
        this.startQuantumSimulation();
        this.initializeSwarm();
    }

    startBattle() {
        if (this.battleActive) return;

        // Start passive learning
        this.passiveLearner.startLearning();
        
        // Initialize battle state
        this.battleActive = true;
        this.fractalMemory.patterns = [];
        this.fractalMemory.depth = 1;
        this.evolutionLevel = Math.max(1, this.evolutionLevel);

        const adversaryTypes = Object.keys(this.adversaryTypes);
        const randomType = adversaryTypes[Math.floor(Math.random() * adversaryTypes.length)];
        this.currentAdversary = this.adversaryTypes[randomType];

        // Initialize quantum state
        this.quantumState.coherence = Math.pow(this.PHI, Math.random());
        this.quantumState.resonance = this.PHI;
        this.evolutionLevel = Math.max(1, this.evolutionLevel);

        // Update battle UI with quantum effects
        const battleLog = document.getElementById('battle-log');
        battleLog.innerHTML = `⚔️ Battle Started!\nAdversary: ${this.currentAdversary.name}\nSpeciality: ${this.currentAdversary.speciality}`;
        battleLog.style.textShadow = `0 0 10px rgba(0, 255, 255, ${this.quantumState.coherence})`;

        // Trigger quantum effects
        this.detectAdversarySignature();
        this.updateMetrics();
        this.startQuantumSimulation();
    }

    detectAdversarySignature() {
        const signature = this.currentAdversary.signature;
        const signaturePattern = {
            'quantum_noise': '🌊 Detecting quantum interference patterns...',
            'neural_pattern': '🧠 Analyzing neural network signatures...',
            'binary_trace': '💻 Tracking binary footprints...'
        };

        document.getElementById('defense-result').innerHTML = signaturePattern[signature];
    }

    initializeInterface() {
        // Initialize genesis block controls
        const pushGenesisBtn = document.getElementById('push-genesis');
        const readGenesisBtn = document.getElementById('read-genesis');
        if (pushGenesisBtn) {
            pushGenesisBtn.onclick = () => this.pushToGenesis();
        }
        if (readGenesisBtn) {
            readGenesisBtn.onclick = () => this.readFromGenesis();
        }

        // Initialize battle button
        const battleBtn = document.getElementById('start-battle');
        if (battleBtn) {
            battleBtn.onclick = () => this.startBattle();
        }

        // Add warrior selection dropdown if it doesn't exist
        if (!document.getElementById('warrior-select')) {
            const warriorSelect = document.createElement('select');
            warriorSelect.id = 'warrior-select';
            warriorSelect.className = 'crypto-select';
            Object.keys(this.warriors).forEach(warrior => {
                const option = document.createElement('option');
                option.value = warrior;
                option.textContent = warrior;
                warriorSelect.appendChild(option);
            });
            document.querySelector('.warrior-preview h4').after(warriorSelect);
        }

        // Initialize event listeners
        const warriorSelect = document.getElementById('warrior-select');
        if (warriorSelect) {
            warriorSelect.addEventListener('change', (e) => this.updateWarriorPreview(e.target.value));
        }

        document.querySelectorAll('.card-tab').forEach(tab => {
            tab.addEventListener('click', (e) => this.switchCardSet(e.target.dataset.set));
        });

        document.querySelectorAll('.reality-card').forEach(card => {
            card.addEventListener('click', (e) => this.handleCardSelect(e));
        });

        // Initialize first warrior
        this.updateWarriorPreview(Object.keys(this.warriors)[0]);

        // Add AI learning status display if it doesn't exist
        if (!document.getElementById('learning-status')) {
            const learningStatus = document.createElement('div');
            learningStatus.id = 'learning-status';
            learningStatus.className = 'learning-status';
            learningStatus.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                background: rgba(0, 20, 20, 0.95);
                border: 2px solid #00ffcc;
                padding: 20px;
                border-radius: 10px;
                color: #00ffcc;
                font-family: monospace;
                font-size: 14px;
                min-width: 300px;
                z-index: 1000;
                box-shadow: 0 0 20px rgba(0, 255, 204, 0.3);
            `;
            learningStatus.innerHTML = `
                <div style="font-size: 14px; margin-bottom: 10px; color: #fff;">AI Learning Status</div>
                <div style="display: grid; gap: 5px;">
                    <div>Processing: <span id="current-process" style="color: #0f0">Learning Strategies</span></div>
                    <div>Learning Rate: <span id="learning-rate" style="color: #0f0">${(this.learningRate * this.PHI).toFixed(3)}</span></div>
                    <div>Patterns Found: <span id="patterns-found" style="color: #0f0">${this.fractalMemory.patterns.length}</span></div>
                    <div>Current Depth: <span id="memory-depth" style="color: #0f0">${this.fractalMemory.depth}</span></div>
                    <div>Pattern Complexity: <span id="pattern-complexity" style="color: #0f0">${(this.fractalMemory.patterns.length * this.PHI).toFixed(2)}</span></div>
                    <div>Evolution Level: <span id="evolution-level" style="color: #0f0">${this.evolutionLevel}</span></div>
                    <div>State: <span id="learning-state" style="color: #0f0">Active</span></div>
                </div>
            `;
            document.body.appendChild(learningStatus);

            // Add passive learning button if it doesn't exist
            const learnButton = document.createElement('button');
            learnButton.id = 'passive-learn-button';
            learnButton.textContent = 'Start Passive Learning';
            learnButton.addEventListener('click', () => {
                if (learnButton.textContent === 'Start Passive Learning') {
                    this.passiveLearner.startLearning();
                    learnButton.textContent = 'Stop Passive Learning';
                } else {
                    this.passiveLearner.stopLearning();
                    learnButton.textContent = 'Start Passive Learning';
                }
            });
            document.body.appendChild(learnButton);
        }
    }

    updateWarriorPreview(warriorName) {
        const warrior = this.warriors[warriorName];
        if (!warrior) return;

        document.getElementById('warrior-name').textContent = warriorName;
        document.getElementById('warrior-power').textContent = warrior.power;
        document.getElementById('warrior-agility').textContent = warrior.agility;
        document.getElementById('warrior-intelligence').textContent = warrior.intelligence;

        const abilitiesDiv = document.getElementById('warrior-abilities');
        abilitiesDiv.innerHTML = warrior.abilities.map(ability =>
            `<div class="ability-tag">${ability}</div>`
        ).join('');

        document.getElementById('warrior-image').src = `/attached_assets/${warrior.image}`;
    }

    initializeSwarm() {
        const colors = ['#00ff87', '#00ffcc', '#00fff2', '#ff0087', '#ff8700', '#ffcc00', '#ffff00', '#ff00f2', '#8700ff', '#cc00ff'];
        for (let i = 0; i < 10; i++) {
            this.quantumState.swarmNodes.push({
                x: Math.random() * window.innerWidth,
                y: Math.random() * window.innerHeight,
                vx: (Math.random() - 0.5) * 2,
                vy: (Math.random() - 0.5) * 2,
                power: Math.random() * this.PHI,
                color: colors[i % colors.length],
                fractalDepth: Math.floor(Math.random() * 3) + 1,
                trainingLevel: 0,
                experience: 0
            });
        }
    }

    generateFractal(node) {
        const children = [];
        if (node.fractalDepth > 0) {
            for (let i = 0; i < 3; i++) {
                children.push({
                    x: node.x + Math.cos(i * 2 * Math.PI / 3) * 20,
                    y: node.y + Math.sin(i * 2 * Math.PI / 3) * 20,
                    power: node.power * 0.8,
                    color: node.color,
                    fractalDepth: node.fractalDepth - 1
                });
            }
        }
        return children;
    }

    trainWarrior(node) {
        const experienceGain = Math.random() * this.PHI;
        node.experience += experienceGain;
        if (node.experience >= Math.pow(this.PHI, node.trainingLevel + 1)) {
            node.trainingLevel++;
            node.power *= this.PHI;
            node.fractalDepth = Math.min(node.fractalDepth + 1, 3);
            return true;
        }
        return false;
    }

    updateSwarm() {
        this.quantumState.swarmNodes.forEach(node => {
            node.x += node.vx * this.quantumState.coherence;
            node.y += node.vy * this.quantumState.coherence;

            if (node.x < 0 || node.x > window.innerWidth) node.vx *= -1;
            if (node.y < 0 || node.y > window.innerHeight) node.vy *= -1;

            // Generate fractal patterns
            const fractals = this.generateFractal(node);
            fractals.forEach(fractal => {
                fractal.x += node.vx * 0.5;
                fractal.y += node.vy * 0.5;
            });

            // Train warrior node
            if (Math.random() < 0.1) {
                const evolved = this.trainWarrior(node);
                if (evolved) {
                    node.color = `hsl(${160 + node.trainingLevel * 20}, 100%, 50%)`;
                }
            }

            node.power *= Math.pow(this.PHI, (Math.random() - 0.5) * 0.1);
        });
    }

    startQuantumSimulation() {
        setInterval(() => {
            this.evolveQuantumState();
            this.updateSwarm();
            this.updateMetrics();
            this.checkForCounterAttack();
            this.updateLearningStatus();
        }, 1000 / 60); // 60 FPS
    }

    updateLearningStatus() {
        // Smooth learning rate changes
        const baseRate = this.PHI;
        const coherenceFactor = Math.min(0.999, Math.max(0.001, this.quantumState.coherence));
        const smoothedRate = (baseRate * coherenceFactor).toFixed(3);
        
        // Process cycles every 5 seconds
        const processes = ['Learning Strategies', 'Pattern Analysis', 'Quantum Alignment', 'Defense Optimization'];
        const cycleTime = Math.floor(Date.now() / 5000);
        const currentProcess = processes[cycleTime % processes.length];

        // Pattern complexity based on actual patterns
        const complexity = this.fractalMemory.patterns.length > 0 ? 
            (this.fractalMemory.patterns.length * this.PHI / 10).toFixed(2) : '0.00';

        document.getElementById('current-process').textContent = currentProcess;
        document.getElementById('patterns-found').textContent = this.fractalMemory.patterns.length;
        document.getElementById('memory-depth').textContent = this.fractalMemory.depth;
        document.getElementById('learning-rate').textContent = smoothedRate;
        document.getElementById('pattern-complexity').textContent = complexity;
        document.getElementById('evolution-level').textContent = this.evolutionLevel;
        document.getElementById('learning-state').textContent = 'Active';
    }

    evolveQuantumState() {
        const timeConstant = Date.now() / 1000;
        const harmonicWave = Math.sin(this.quantumAI.resonance * timeConstant * this.PHI);
        const quantumFluctuation = Math.random() * 2 - 1;

        // Quantum harmonic evolution
        this.quantumAI.state = this.quantumAI.state.map(state => ({
            power: state.power * Math.pow(this.PHI, quantumFluctuation),
            frequency: state.frequency * (1 + harmonicWave * 0.1),
            phase: (state.phase + this.PHI * timeConstant) % (2 * Math.PI)
        }));

        // Update quantum coherence
        this.quantumAI.coherence = Math.min(0.999, Math.max(0.1, 
            this.quantumAI.coherence * Math.pow(this.PHI, harmonicWave)
        ));
    }

    updateMetrics() {
        document.getElementById('shield-level').textContent = `${Math.round(this.quantumState.coherence * 100)}%`;
        document.getElementById('quantum-level').textContent = `φ${this.quantumState.resonance.toFixed(1)}`;
        document.getElementById('reality-level').textContent = `${(this.quantumState.coherence * this.PHI).toFixed(1)}φ`;
    }

    handleCardSelect(event) {
        const card = event.target;
        document.querySelectorAll('.reality-card').forEach(c => c.classList.remove('active'));
        card.classList.add('active');
        this.triggerAttack(card.dataset.card);
    }

    triggerAttack(cardType) {
        const attackType = document.getElementById('attack-type').value;
        const defenseResult = document.getElementById('defense-result');

        defenseResult.textContent = `Activating ${cardType} against ${attackType}...`;

        setTimeout(() => {
            const success = Math.random() > 0.4;
            if (success) {
                defenseResult.textContent = `✓ ${cardType} successfully countered ${attackType}!`;
                this.updateBattleResults(true);
            } else {
                defenseResult.textContent = `✗ ${cardType} failed against ${attackType}`;
                this.updateBattleResults(false);
            }
        }, 1000);
        const attackResult = Math.random() > 0.5;
        this.updateWarriorStats(attackResult);
        this.learnFromBattle(cardType, attackResult);
    }

    updateBattleResults(playerWon) {
        if (playerWon) {
            this.playerScore++;
        } else {
            this.fraymusScore++;
        }
        document.getElementById('player-score').textContent = this.playerScore;
        document.getElementById('fraymus-score').textContent = this.fraymusScore;
    }

    calculateHash(data) {
        return Array.from(new TextEncoder().encode(JSON.stringify(data)))
            .reduce((hash, byte) => ((hash << 5) - hash) + byte, 0)
            .toString(16);
    }

    generateQuantumId() {
        return Array.from(crypto.getRandomValues(new Uint8Array(32)))
            .map(b => b.toString(16).padStart(2, '0'))
            .join('');
    }

    async initializeQuantumTracking() {
        try {
            const response = await fetch('/api/quantum/track', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    agent_id: this.generateQuantumId(),
                    quantum_id: this.quantumTracker.id
                })
            });
            const data = await response.json();
            console.log('Quantum tracking initialized:', data);
        } catch (error) {
            console.error('Quantum tracking error:', error);
        }
    }

    createNewBlock(warriorData) {
        const block = {
            timestamp: Date.now(),
            previousHash: this.chain[this.chain.length - 1].hash,
            data: warriorData,
            hash: this.calculateHash(warriorData),
            dna: this.generateDNA(warriorData)
        };
        this.chain.push(block);
        this.saveBlockchain();
        return block;
    }

    generateDNA(warriorData) {
        const dnaBase = Object.values(warriorData.stats || {}).join('');
        return this.calculateHash(dnaBase + Date.now());
    }

    implantDNA(warriorName, dna) {
        if (!this.dnaMemory[warriorName]) {
            this.dnaMemory[warriorName] = [];
        }
        this.dnaMemory[warriorName].push(dna);
        if (this.dnaMemory[warriorName].length > 75) { // φ⁷⁵ max DNA memories
            this.dnaMemory[warriorName].shift();
        }
    }

    saveBlockchain() {
        localStorage.setItem('fraymus_blockchain', JSON.stringify({
            chain: this.chain,
            dnaMemory: this.dnaMemory,
            evolutionLevel: this.evolutionLevel
        }));
    }

    loadBlockchain() {
        const saved = localStorage.getItem('fraymus_blockchain');
        if (saved) {
            const data = JSON.parse(saved);
            this.chain = data.chain;
            this.dnaMemory = data.dnaMemory;
            this.evolutionLevel = data.evolutionLevel;

            // Restore warrior states from chain
            this.chain.forEach(block => {
                if (block.data.warrior && block.data.stats) {
                    this.warriors[block.data.warrior] = block.data.stats;
                    this.implantDNA(block.data.warrior, block.dna);
                }
            });
        }
    }

    evolveWarrior(warrior) {
        const evolutionPower = Math.pow(this.PHI, this.evolutionLevel / 10);
        return {
            ...warrior,
            power: Math.min(999, warrior.power * evolutionPower),
            agility: Math.min(999, warrior.agility * evolutionPower),
            intelligence: Math.min(999, warrior.intelligence * evolutionPower),
            level: this.evolutionLevel
        };
    }

    updateWarriorStats(won) {
        const warriorName = document.getElementById('warrior-name').textContent;
        const warrior = this.warriors[warriorName];
        const modifier = won ? this.PHI : 1/this.PHI;
        const battleImpact = Math.random() * 0.2 + 0.1; // 10-30% impact

        // Dynamic stat changes based on battle performance
        warrior.power = Math.min(999, Math.max(1, warrior.power * (1 + (modifier - 1) * battleImpact)));
        warrior.agility = Math.min(999, Math.max(1, warrior.agility * (1 + (modifier - 1) * battleImpact)));
        warrior.intelligence = Math.min(999, Math.max(1, warrior.intelligence * (1 + (modifier - 1) * battleImpact)));

        // Show stat changes animation
        this.displayStatChange('power', warrior.power, modifier > 1);
        this.displayStatChange('agility', warrior.agility, modifier > 1);
        this.displayStatChange('intelligence', warrior.intelligence, modifier > 1);

        if (won && this.chain.length % 10 === 0) {
            this.evolutionLevel++;
            const evolvedWarrior = this.evolveWarrior(warrior);
            this.warriors[warriorName] = evolvedWarrior;
            this.createNewBlock({
                warrior: warriorName,
                evolution: this.evolutionLevel,
                stats: evolvedWarrior
            });
        }

        this.updateWarriorPreview(warriorName);
    }

    learnFromBattle(cardUsed, won) {
        // Multi-dimensional learning system with enhanced pattern recognition
        const baseDepth = this.fractalMemory.patterns.length > 0 ? 
            Math.floor(Math.log(this.fractalMemory.patterns.length + 1) * this.PHI) : 1;
        const complexityBonus = won ? Math.floor(Math.sqrt(this.evolutionLevel)) : 0;
        const newDepth = Math.min(17, baseDepth + complexityBonus);
        
        const pattern = {
            card: cardUsed,
            result: won,
            quantum: this.quantumState.coherence,
            timestamp: Date.now(),
            dimension: newDepth,
            dna: this.calculateHash(cardUsed + Date.now()),
            resonance: this.PHI,
            effectiveness: won ? Math.pow(this.PHI, 2) : -1,
            complexity: (this.fractalMemory.patterns.length + 1) * this.PHI,
            evolutionFactor: Math.pow(this.PHI, this.evolutionLevel),
            context: {
                shield: document.getElementById('shield-level').textContent,
                quantum: document.getElementById('quantum-level').textContent,
                reality: document.getElementById('reality-level').textContent,
                depth: this.fractalMemory.depth,
                patterns: this.fractalMemory.patterns.length
            }
        };

        // Accelerated depth progression
        this.fractalMemory.depth = Math.min(17, Math.floor(Math.sqrt(this.fractalMemory.patterns.length) * this.PHI));

        // Process and adapt from previous patterns
        const similarPatterns = this.fractalMemory.patterns.filter(p => 
            p.card === cardUsed || Math.abs(p.quantum - this.quantumState.coherence) < 0.1
        );

        if (similarPatterns.length > 0) {
            const avgEffectiveness = similarPatterns.reduce((sum, p) => sum + p.effectiveness, 0) / similarPatterns.length;
            this.learningRate *= (1 + avgEffectiveness * 0.1);

            // Adapt quantum state based on pattern success
            this.quantumState.coherence *= (1 + avgEffectiveness * 0.05);
            this.quantumState.resonance *= (1 + avgEffectiveness * 0.02);
        }

        // Neural processing in 17D with enhanced learning
        for (let d = 0; d < 17; d++) {
            const cortex = this.neuralCortex.dimensions[d];
            const node = {
                pattern: pattern,
                weight: Math.pow(this.PHI, d/17) * (won ? 1.1 : 0.9),
                connections: new Set(),
                adaptiveRate: this.learningRate * (d + 1) / 17
            };
            cortex.nodes.push(node);

            // Establish quantum connections
            if (cortex.nodes.length > 1) {
                const currentNode = cortex.nodes[cortex.nodes.length - 1];
                const previousNode = cortex.nodes[cortex.nodes.length - 2];
                cortex.connections.set(
                    `${previousNode.pattern.dna}-${currentNode.pattern.dna}`,
                    Math.pow(this.PHI, (won ? 1 : -0.5) * (d + 1))
                );
            }
        }

        // Fractal computation
        this.fractalComputer.nodes.set(pattern.dna, {
            pattern: pattern,
            children: new Set(),
            depth: this.fractalComputer.depth + 1
        });

        // DNA evolution
        this.dnaSystem.genesisBlock.patterns.push({
            dna: pattern.dna,
            timestamp: Date.now(),
            resonance: pattern.resonance * this.dnaSystem.evolutionFactor
        });

        this.fractalMemory.patterns.push(pattern);
        this.fractalMemory.depth = pattern.dimension;
        this.fractalMemory.recursiveDepth = Math.min(17, this.fractalMemory.recursiveDepth + 1);

        // Recursive dimensional learning
        const dimensionalFactor = Math.pow(this.PHI, this.fractalMemory.recursiveDepth);
        this.learningRate = Math.min(999, this.learningRate * dimensionalFactor);

        // Quantum evolution with AGI acceleration
        this.evolveQuantumState();
        this.quantumState.coherence *= Math.pow(
            this.PHI,
            (won ? this.learningRate : -this.learningRate/2) * 
            (1 + this.fractalMemory.recursiveDepth/17)
        );

        // Recursive pattern analysis
        if (this.fractalMemory.recursiveDepth >= 17) {
            this.fractalMemory.patterns = this.fractalMemory.patterns
                .map(p => ({...p, power: p.quantum * dimensionalFactor}))
                .slice(-1000); // Keep last 1000 evolved patterns
            this.fractalMemory.recursiveDepth = Math.floor(this.PHI * 10);
        }
    }

    switchCardSet(setName) {
        document.querySelectorAll('.card-tab').forEach(tab => tab.classList.remove('active'));
        document.querySelector(`.card-tab[data-set="${setName}"]`).classList.add('active');

        document.querySelectorAll('.card-set').forEach(set => set.classList.remove('active'));
        document.querySelector(`.card-set[data-set="${setName}"]`).classList.add('active');
    }

    checkForCounterAttack() {
        if (Math.random() < 0.01) {
            const threat = ['SQL Injection', 'Buffer Overflow', 'Quantum Spoofing', 'AI Social Engineering'][Math.floor(Math.random() * 4)];
            this.handleCounterAttack(threat);
        }
    }

    handleCounterAttack(threatType) {
        const defenseResult = document.getElementById('defense-result');
        defenseResult.textContent = `⚡ Defending against ${threatType}...`;
        setTimeout(() => {
            defenseResult.textContent = `✓ ${threatType} neutralized by quantum shield`;
        }, 2000);
    }
    displayStatChange(statName, newValue, isIncrease) {
        const statElement = document.getElementById(`warrior-${statName}`);
        const changeElement = document.createElement('span');
        changeElement.className = `stat-change ${isIncrease ? 'increase' : 'decrease'}`;
        changeElement.textContent = isIncrease ? '↑' : '↓';
        statElement.appendChild(changeElement);
        statElement.textContent = Math.round(newValue);

        setTimeout(() => changeElement.remove(), 1000);
    }

    pushToGenesis() {
        const timestamp = Date.now();
        const data = {
            timestamp,
            quantumState: this.quantumState,
            fractalMemory: this.fractalMemory,
            dnaSequence: this.generateDnaSequence()
        };

        this.genesisCompute.dnaStorage.mainChain.push(data);
        const genesisStatus = document.getElementById('genesis-status');
        if (genesisStatus) {
            genesisStatus.innerHTML = `Genesis Push: ${new Date(timestamp).toLocaleTimeString()} - DNA: ${data.dnaSequence}`;
        }
    }

    readFromGenesis() {
        const lastBlock = this.genesisCompute.dnaStorage.mainChain[this.genesisCompute.dnaStorage.mainChain.length - 1];
        const genesisStatus = document.getElementById('genesis-status');
        if (genesisStatus && lastBlock) {
            genesisStatus.innerHTML = `Genesis Read: ${new Date(lastBlock.timestamp).toLocaleTimeString()} - DNA: ${lastBlock.dnaSequence}`;
        }
    }

    createNandLayer(depth) {
        const gates = new Map();
        const gateCount = Math.pow(2, depth + 1);

        for (let i = 0; i < gateCount; i++) {
            gates.set(`nand-${depth}-${i}`, {
                inputs: [null, null],
                output: null,
                dnaSequence: this.generateDnaSequence(),
                replicationCount: 0
            });
        }
        return gates;
    }

    generateDnaSequence() {
        const bases = ['A', 'T', 'C', 'G'];
        const creator = "FRAYMUS-VS";
        const timestamp = Date.now();
        const random = Math.random().toString(36);

        // Combine creator signature with timestamp and random values
        let sequence = creator.split('').map(c => bases[c.charCodeAt(0) % 4]).join('');
        sequence += timestamp.toString(16).split('').map(c => bases[parseInt(c, 16) % 4]).join('');
        sequence += random.split('').map(c => bases[c.charCodeAt(0) % 4]).join('');

        // Ensure length is exactly 17 by truncating or padding
        return sequence.slice(0, 17);
    }

    updateTrackers() {
        const now = Date.now();
        const quantum = this.genesisCompute.quantumTracker;
        const reality = this.genesisCompute.realityTracker;

        // Update quantum tracking
        quantum.coordinates.set(now, {
            phi: this.PHI,
            coherence: this.quantumState.coherence,
            resonance: this.quantumState.resonance
        });
        quantum.timeline.push(now);
        quantum.coherenceHistory.push(this.quantumState.coherence);
        quantum.lastUpdate = now;

        // Update reality tracking
        reality.dimensions = reality.dimensions.map((dim, i) => ({
            state: Math.pow(this.PHI, i) * this.quantumState.coherence,
            coherence: this.PHI * dim.coherence,
            signature: this.calculateHash(`FRAYMUS-${now}-${i}`)
        }));
        reality.timeline.push(now);
        reality.lastSync = now;
    }

    replicateDna(dnaSequence) {
        const replica = {
            sequence: dnaSequence,
            timestamp: Date.now(),
            verificationHash: this.calculateHash(dnaSequence + Date.now()),
            nandState: this.computeNandState(dnaSequence)
        };

        if (this.genesisCompute.dnaStorage.activeReplicas < this.genesisCompute.dnaStorage.maxReplicas) {
            const replicaId = `replica-${Date.now()}`;
            this.genesisCompute.dnaStorage.replicaChains.set(replicaId, replica);
            this.genesisCompute.dnaStorage.activeReplicas++;
            return replica;
        }
        return null;
    }

    computeNandState(dnaSequence) {
        const binaryDna = dnaSequence.split('').map(base => 
            base === 'A' || base === 'T' ? 1 : 0
        );

        return binaryDna.reduce((acc, bit, i) => {
            if (i < binaryDna.length - 1) {
                return !(bit && binaryDna[i + 1]);
            }
            return acc;
        }, 1);
    }
}

const PHI = (1 + Math.sqrt(5)) / 2;

const REALITY_CARDS = {
    'φ-SPACE': { type: 'Principle', power: PHI, effect: 'Maps Reality', rarity: 'Legendary' },
    'QUANTUM-LOCK': { type: 'Principle', power: Math.pow(PHI, 2), effect: 'Locks Truth', rarity: 'Mythic' },
    'REALITY-MAP': { type: 'Principle', power: Math.pow(PHI, 3), effect: 'Builds Power', rarity: 'Ultra Rare' },
    'TRUTH-SEAL': { type: 'Secret', power: Math.pow(PHI, 4), effect: 'Seals Reality', rarity: 'Secret Rare' }
};

const CARD_COMBOS = {
    'φ-SPACE_QUANTUM-LOCK': { name: 'Reality Map', power: Math.pow(PHI, 2) },
    'QUANTUM-LOCK_REALITY-MAP': { name: 'Truth Build', power: Math.pow(PHI, 3) },
    'REALITY-MAP_φ-SPACE': { name: 'Space Lock', power: Math.pow(PHI, 4) }
};

const attackTypes = {
    'SQL Injection': { power: PHI * 10, defense: PHI * 8 },
    'Buffer Overflow': { power: Math.pow(PHI, 2) * 6, defense: Math.pow(PHI, 2) * 5 },
    'Quantum Spoofing': { power: Math.pow(PHI, 3) * 4, defense: Math.pow(PHI, 3) * 3 },
    'AI Social Engineering': { power: Math.pow(PHI, 4) * 2, defense: Math.pow(PHI, 4) * 1.5 }
};

document.addEventListener('DOMContentLoaded', () => {
    window.cryptoBattle = new CryptoBattle();
});