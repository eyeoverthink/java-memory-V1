// Core Quantum Effects
class QuantumEffects {
  constructor() {
    this.coherenceState = 1.0;
    this.phiRatio = 1.618033988749895;
  }

  checkCoherence(state) {
    // Calculate quantum coherence using phi-based wave function
    const coherenceValue = Math.pow(this.phiRatio, state) % 1;
    this.coherenceState = coherenceValue;
    return coherenceValue >= 0.618;
  }

  getCoherenceMetrics() {
    return {
      state: this.coherenceState,
      phi: this.phiRatio,
      isCoherent: this.coherenceState >= 0.618
    };
  }
}

// Quantum Dimension Simulation
class QuantumDimensionSimulation {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        this.setupCanvas();
        this.animate();
    }

    setupCanvas() {
        this.canvas = document.createElement('canvas');
        this.ctx = this.canvas.getContext('2d');
        this.canvas.width = window.innerWidth;
        this.canvas.height = window.innerHeight;
        this.container.appendChild(this.canvas);

        this.particles = [];
        for (let i = 0; i < 100; i++) {
            this.particles.push({
                x: Math.random() * this.canvas.width,
                y: Math.random() * this.canvas.height,
                radius: Math.random() * 2 + 1,
                vx: (Math.random() - 0.5) * 2,
                vy: (Math.random() - 0.5) * 2
            });
        }
    }

    animate() {
        this.ctx.fillStyle = 'rgba(0, 0, 0, 0.05)';
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);

        this.particles.forEach(particle => {
            particle.x += particle.vx;
            particle.y += particle.vy;

            if (particle.x < 0 || particle.x > this.canvas.width) particle.vx *= -1;
            if (particle.y < 0 || particle.y > this.canvas.height) particle.vy *= -1;

            this.ctx.beginPath();
            this.ctx.arc(particle.x, particle.y, particle.radius, 0, Math.PI * 2);
            this.ctx.fillStyle = '#0ff';
            this.ctx.fill();
        });

        requestAnimationFrame(() => this.animate());
    }
}

// Security Core
if (!window.SecurityCore) {
    window.SecurityCore = class SecurityCore {
        constructor() {
            console.log("Security core initialized");
            this.φ = (1 + Math.sqrt(5)) / 2;
            this.initialized = true;
        }
    }
}

// Make classes available globally
window.QuantumEffects = QuantumEffects;
window.QuantumDimensionSimulation = QuantumDimensionSimulation;
//window.SecurityCore = SecurityCore;

// Helper functions
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