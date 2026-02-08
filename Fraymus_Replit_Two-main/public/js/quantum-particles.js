
class QuantumParticleSystem {
    constructor() {
        this.φ = (1 + Math.sqrt(5)) / 2;
        this.canvas = document.createElement('canvas');
        this.ctx = this.canvas.getContext('2d');
        this.particles = [];
        this.ℏ = 1.054571817e-34; // Planck constant
        
        this.quantumFields = Array(17).fill(null).map((_, i) => ({
            angle: (Math.PI * 2 * i) / 17,
            scale: 50 + Math.random() * 50,
            rotation: Math.random() * Math.PI * 2,
            consciousness: Math.random(),
            potential: Math.random() * this.φ
        }));
        
        this.canvas.style.position = 'fixed';
        this.canvas.style.top = '0';
        this.canvas.style.left = '0';
        this.canvas.style.width = '100%';
        this.canvas.style.height = '100%';
        this.canvas.style.zIndex = '-2';
        this.canvas.style.opacity = '0.8';
        
        document.body.insertBefore(this.canvas, document.body.firstChild);
        this.resize();
        window.addEventListener('resize', () => this.resize());
        
        this.init();
        this.animate();
    }

    resize() {
        this.canvas.width = window.innerWidth;
        this.canvas.height = window.innerHeight;
    }

    init() {
        for (let i = 0; i < 150; i++) {
            this.particles.push({
                x: Math.random() * this.canvas.width,
                y: Math.random() * this.canvas.height,
                z: Math.random() * 100,
                phase: Math.random() * Math.PI * 2,
                consciousness: Math.random(),
                waveFunction: Math.random() * this.φ,
                velocity: {
                    x: (Math.random() - 0.5) * 2,
                    y: (Math.random() - 0.5) * 2,
                    z: (Math.random() - 0.5) * 2
                }
            });
        }
    }

    computeSchrodingerState(particle, time) {
        const consciousness = particle.consciousness;
        const phase = particle.phase + time * 0.001;
        const waveFunction = particle.waveFunction;
        
        // Implementing Schrödinger-Consciousness equation
        const potential = this.computePotential(particle);
        const kineticTerm = -0.5 * this.ℏ * (particle.velocity.x ** 2 + particle.velocity.y ** 2);
        const consciousnessTerm = consciousness * Math.sin(phase);
        
        return {
            amplitude: Math.exp(-Math.abs(consciousness - 0.5)) * 
                      Math.sin(waveFunction * phase + kineticTerm),
            probability: consciousness * (1 + Math.sin(phase + potential)),
            coherence: Math.exp(-potential / (2 * this.φ))
        };
    }

    computePotential(particle) {
        return this.quantumFields.reduce((pot, field) => {
            const dx = Math.cos(field.angle) * field.scale - particle.x;
            const dy = Math.sin(field.angle) * field.scale - particle.y;
            const distance = Math.sqrt(dx * dx + dy * dy);
            return pot + field.potential / (distance + 1);
        }, 0);
    }

    drawMandelbrotPattern(x, y, scale, maxIter = 100) {
        const zx = (x - this.canvas.width/2) / scale;
        const zy = (y - this.canvas.height/2) / scale;
        let cx = zx;
        let cy = zy;
        
        for (let i = 0; i < maxIter; i++) {
            const x2 = cx * cx;
            const y2 = cy * cy;
            if (x2 + y2 > 4) return i;
            const xtemp = x2 - y2 + zx;
            cy = 2 * cx * cy + zy;
            cx = xtemp;
        }
        return maxIter;
    }

    animate() {
        const time = performance.now();
        
        this.ctx.fillStyle = 'rgba(0, 0, 0, 0.1)';
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);

        // Update quantum fields
        this.quantumFields.forEach(field => {
            field.rotation += 0.002;
            const centerX = this.canvas.width/2 + Math.cos(field.angle) * 200;
            const centerY = this.canvas.height/2 + Math.sin(field.angle) * 200;
            
            // Draw quantum field influence
            const iter = this.drawMandelbrotPattern(centerX, centerY, 200);
            const hue = (iter * 2) % 360;
            this.ctx.beginPath();
            this.ctx.arc(centerX, centerY, field.scale * field.consciousness, 0, Math.PI * 2);
            this.ctx.fillStyle = `hsla(${hue}, 100%, 50%, ${0.1 * field.consciousness})`;
            this.ctx.fill();
        });

        // Update and draw particles
        this.particles.forEach(p => {
            const state = this.computeSchrodingerState(p, time);
            
            // Update position with quantum influence
            p.x += p.velocity.x * state.probability;
            p.y += p.velocity.y * state.probability;
            p.z += p.velocity.z * state.probability;
            p.phase += 0.05;
            p.waveFunction += 0.01 * (Math.random() - 0.5);

            // Boundary checks with φ-space wrapping
            if (p.x < 0) p.x = this.canvas.width;
            if (p.x > this.canvas.width) p.x = 0;
            if (p.y < 0) p.y = this.canvas.height;
            if (p.y > this.canvas.height) p.y = 0;
            if (p.z < 0 || p.z > 100) p.velocity.z *= -1;

            // Draw particle with quantum state influence
            const size = (2 + state.amplitude) * (1 + p.z/100) * state.coherence;
            this.ctx.beginPath();
            this.ctx.arc(p.x, p.y, size, 0, Math.PI * 2);
            
            const hue = 180 + 60 * state.probability;
            const alpha = 0.5 * state.coherence * (1 + state.amplitude);
            this.ctx.fillStyle = `hsla(${hue}, 100%, 50%, ${alpha})`;
            this.ctx.fill();
        });

        requestAnimationFrame(() => this.animate());
    }
}

export default QuantumParticleSystem;
