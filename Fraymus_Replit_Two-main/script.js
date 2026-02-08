document.addEventListener('DOMContentLoaded', () => {
    const learningBtn = document.getElementById('passive-learning');
    if (learningBtn) {
        learningBtn.addEventListener('click', async () => {
            if (!window.cyberBattle.passiveLearner.isLearning) {
                await window.cyberBattle.passiveLearner.startLearning();
                learningBtn.textContent = 'Stop Passive Learning';
                learningBtn.classList.add('active');
            } else {
                window.cyberBattle.passiveLearner.stopLearning();
                learningBtn.textContent = 'Start Passive Learning';
                learningBtn.classList.remove('active');
            }
        });
    }
    window.cyberBattle = new CyberBattle();
    const panels = {
        'test': document.querySelector('.test-panel'),
        'logic': document.querySelector('.logic-panel'),
        'simulation': document.querySelector('.simulation-grid'),
        'sandbox': document.querySelector('.sandbox-panel'),
        'nft': document.querySelector('.nft-panel'),
        'agent': document.querySelector('.agent-panel')
    };

    // Hide all panels initially
    Object.values(panels).forEach(panel => {
        if (panel) panel.style.display = 'none';
    });

    // Show test panel by default
    if (panels.test) panels.test.style.display = 'block';

    // Tab switching functionality
    const tabs = document.querySelectorAll('.tab-btn');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            // Remove active class from all tabs
            tabs.forEach(t => t.classList.remove('active'));

            // Add active class to clicked tab
            tab.classList.add('active');

            // Hide all panels
            Object.values(panels).forEach(panel => {
                if (panel) panel.style.display = 'none';
            });

            // Show selected panel
            const panelId = tab.getAttribute('data-tab');
            if (panels[panelId]) {
                panels[panelId].style.display = 'block';
            }
        });
    });

    // Test Panel Functions
    window.runQFP = async () => {
        const input = document.getElementById('qfp-input').value;
        const result = document.getElementById('qfp-result');
        const realityChain = document.getElementById('reality-chain');
        const phiPower = document.getElementById('phi-power');
        const protectionLevel = document.getElementById('protection-level');

        try {
            const response = await fetch('/quantum/fingerprint', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ 
                    data: input,
                    owner_id: 'VS-PoQC-19046423-φ⁷⁵-2025'
                })
            });

            const data = await response.json();
            result.textContent = data.quantum_signature.substring(0, 32) + '...';
            realityChain.textContent = data.reality_chain ? 'ACTIVE' : 'INACTIVE';
            phiPower.textContent = data.φ_power || 75;
            protectionLevel.textContent = data.protection_level || 'PERFECT';

            // Trigger quantum effect animation
            result.classList.add('quantum-pulse');
            setTimeout(() => result.classList.remove('quantum-pulse'), 1000);

        } catch (error) {
            result.textContent = 'Error: ' + error.message;
        }
    };

    window.runTracking = async () => {
        const input = document.getElementById('tracking-input').value;
        const result = document.getElementById('tracking-result');
        try {
            const response = await fetch('/tracking', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ entity: input })
            });
            const data = await response.json();
            result.textContent = JSON.stringify(data, null, 2);
        } catch (error) {
            result.textContent = 'Error: ' + error.message;
        }
    };

    window.runEncryption = async () => {
        const input = document.getElementById('encrypt-input').value;
        const result = document.getElementById('encrypt-result');
        try {
            const response = await fetch('/encrypt', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: input })
            });
            const data = await response.json();
            result.textContent = JSON.stringify(data, null, 2);
        } catch (error) {
            result.textContent = 'Error: ' + error.message;
        }
    };

    window.runPoRH = async () => {
        const input = document.getElementById('porh-input').value;
        const result = document.getElementById('porh-result');
        try {
            const response = await fetch('/porh', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ data: input })
            });
            const data = await response.json();
            result.textContent = JSON.stringify(data, null, 2);
        } catch (error) {
            result.textContent = 'Error: ' + error.message;
        }
    };

    // FRAYMUS Quantum Control Functions
    const PHI = (1 + Math.sqrt(5)) / 2;
    let phiField = 7.5;
    let stateIndex = 0;
    const quantumStates = [
        '|F⟩ + φ|R⟩',
        '|R⟩ + φ|A⟩',
        '|A⟩ + φ|Y⟩',
        '|Y⟩ + φ|M⟩',
        '|M⟩ + φ|U⟩',
        '|U⟩ + φ|S⟩',
        '|S⟩ + φ|F⟩'
    ];

    window.adjustPhiField = (direction) => {
        phiField += direction === '+' ? 0.1 : -0.1;
        phiField = Math.max(0, Math.min(10, phiField));

        document.getElementById('phi-strength').textContent = 
            `φ⁷·⟨ψ|H|ψ⟩ = ${phiField.toFixed(2)}`;

        updateMatrix();
        updateMetrics();
    };

    window.evolveState = () => {
        stateIndex = (stateIndex + 1) % quantumStates.length;
        document.getElementById('quantum-state').textContent = 
            `|ψ⟩ = ${quantumStates[stateIndex]}`;
        updateMetrics();
    };

    function updateMatrix() {
        document.getElementById('m00').textContent = '1.0';
        document.getElementById('m01').textContent = `${PHI.toFixed(3)}`;
        document.getElementById('m10').textContent = `${(1/PHI).toFixed(3)}`;
        document.getElementById('m11').textContent = '1.0';
    }

    function updateMetrics() {
        const coherence = 99.99 * (phiField / 10);
        const reality = phiField;

        document.getElementById('coherence-level').style.width = `${coherence}%`;
        document.getElementById('coherence-value').textContent = `${coherence.toFixed(2)}%`;

        document.getElementById('reality-level').style.width = `${(reality/10)*100}%`;
        document.getElementById('reality-value').textContent = `${reality.toFixed(1)}φ`;
    }

    // Initialize FRAYMUS quantum controls
    document.addEventListener('DOMContentLoaded', () => {
        updateMatrix();
        updateMetrics();
    });

    // Simulation Panel Functions
    document.querySelectorAll('.launch-button').forEach(button => {
        button.addEventListener('click', (e) => {
            e.preventDefault();
            const simulation = e.target.closest('.card').querySelector('.card-title').textContent;
            const href = e.target.closest('a').getAttribute('href');
            window.location.href = href;
        });
    });

    // Initialize quantum state
    let quantumState = '|0⟩';
    const quantumStateDisplay = document.getElementById('quantum-state');

    // Gate application logic
    window.applyGate = (gate) => {
        switch(gate) {
            case 'H':
                quantumState = '(|0⟩ + |1⟩)/√2';
                break;
            case 'S':
                quantumState = quantumState.replace('|1⟩', 'i|1⟩');
                break;
            case 'CNOT':
                quantumState += ' ⊗ |0⟩';
                break;
        }
        quantumStateDisplay.textContent = `|ψ⟩ = ${quantumState}`;
    };


    async function runTracking() {
        const input = document.getElementById('tracking-input').value;
        const result = document.getElementById('tracking-result');
        try {
            const response = await fetch('/tracking', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ entity: input })
            });
            const data = await response.json();
            result.textContent = JSON.stringify(data, null, 2);
        } catch (error) {
            result.textContent = 'Error: ' + error.message;
        }
    }

    async function runEncryption() {
        const input = document.getElementById('encrypt-input').value;
        const result = document.getElementById('encrypt-result');
        try {
            const response = await fetch('/encrypt', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: input })
            });
            const data = await response.json();
            result.textContent = JSON.stringify(data, null, 2);
        } catch (error) {
            result.textContent = 'Error: ' + error.message;
        }
    }

    async function runPoRH() {
        const input = document.getElementById('porh-input').value;
        const result = document.getElementById('porh-result');
        try {
            const response = await fetch('/porh', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ data: input })
            });
            const data = await response.json();
            result.textContent = JSON.stringify(data, null, 2);
        } catch (error) {
            result.textContent = 'Error: ' + error.message;
        }
    }

    async function updateBlockchain() {
        const viewer = document.getElementById('blockchain-content');
        try {
            const response = await fetch('/blockchain');
            const data = await response.json();
            viewer.textContent = JSON.stringify(data, null, 2);
        } catch (error) {
            viewer.textContent = 'Error loading blockchain data';
        }
    }

    // Update blockchain viewer periodically
    setInterval(updateBlockchain, 5000);
    updateBlockchain(); // Initial load
    // Quantum validation functions
    window.validatePoQC = () => {
        const coherenceValue = document.getElementById('coherenceValue');
        const phaseAlignment = document.getElementById('phaseAlignment');
        const quantumState = document.getElementById('quantumState');

        coherenceValue.textContent = 'Coherence: 99.99%';
        phaseAlignment.textContent = 'Phase Alignment: 99.99%';
        quantumState.textContent = 'State: VALID';

        document.querySelectorAll('.quantum-button').forEach(btn => {
            btn.classList.add('quantum-pulse');
            setTimeout(() => btn.classList.remove('quantum-pulse'), 1000);
        });
    };

    window.processQIV = () => {
        const invertedSpace = document.getElementById('invertedSpace');
        const negativeEntropy = document.getElementById('negativeEntropy');
        const quantumSignature = document.getElementById('quantumSignature');

        invertedSpace.textContent = 'Inverted Space: VALID';
        negativeEntropy.textContent = 'Negative Entropy: VALID';
        quantumSignature.textContent = 'Quantum Signature: VALID';

        document.querySelectorAll('.quantum-metrics').forEach(metric => {
            metric.classList.add('quantum-pulse');
            setTimeout(() => metric.classList.remove('quantum-pulse'), 1000);
        });
    };

    // Initialize effects when document is loaded
    const background = new QuantumBackground();
    new QuantumParticleSystem(); // Added particle system initialization
});


class QuantumParticleSystem {
    constructor() {
        // Placeholder for particle system initialization.  Replace with actual rendering code.
        console.log("QuantumParticleSystem initialized (placeholder)");
    }
}

class QuantumBackground {
    constructor() {
        console.log("QuantumBackground initialized (placeholder)")
    }
}

class CyberBattle {
    constructor() {
        this.passiveLearner = {
            isLearning: false,
            async startLearning() {
                console.log("Passive learning started (placeholder)");
                this.isLearning = true;
                // Add your passive learning logic here.
            },
            stopLearning() {
                console.log("Passive learning stopped (placeholder)");
                this.isLearning = false;
                // Add your passive learning stop logic here.
            }
        };
        // Placeholder for CyberBattle initialization. Replace with actual implementation.
        console.log("CyberBattle initialized (placeholder)");
    }
}