
class AnomalyDetection {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        this.canvas = document.createElement('canvas');
        this.canvas.style.width = '100%';
        this.canvas.style.height = '400px';
        this.container.appendChild(this.canvas);
        
        this.ctx = this.canvas.getContext('2d');
        this.dataPoints = [];
        this.anomalies = [];
        this.phi = (1 + Math.sqrt(5)) / 2; // Golden ratio
        this.quantum_state = 1.0;
        this.detection_threshold = 0.618; // Inverse golden ratio
        
        this.init();
        this.animate();
    }
    
    init() {
        // Generate normal data points
        for(let i = 0; i < 100; i++) {
            this.dataPoints.push({
                x: Math.random() * this.canvas.width,
                y: Math.random() * this.canvas.height,
                isAnomalous: false
            });
        }
    }
    
    detectAnomalies() {
        const quantumFluctuation = Math.sin(Date.now() * 0.001 * this.phi) * 0.5 + 0.5;
        if(Math.random() < 0.05 * quantumFluctuation) {
            const angle = Math.random() * Math.PI * 2;
            const radius = Math.random() * this.canvas.width / 3;
            const anomaly = {
                x: this.canvas.width/2 + Math.cos(angle) * radius,
                y: this.canvas.height/2 + Math.sin(angle) * radius,
                isAnomalous: true,
                detected: false,
                intensity: Math.random(),
                phase: Math.random() * Math.PI * 2
            };
            this.dataPoints.push(anomaly);
            
            // Quantum entanglement delay based on phi
            const detectionTime = 1000 * (1 + Math.random() * this.phi);
            setTimeout(() => {
                anomaly.detected = true;
            }, detectionTime);
        }
    }
    
    animate() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        
        // Update quantum state
        const time = Date.now() * 0.001;
        this.quantum_state = 0.8 + Math.sin(time * this.phi) * 0.2;
        
        this.detectAnomalies();
        
        // Draw quantum field
        const gradient = this.ctx.createRadialGradient(
            this.canvas.width/2, this.canvas.height/2, 0,
            this.canvas.width/2, this.canvas.height/2, this.canvas.width/2
        );
        gradient.addColorStop(0, `rgba(0, 255, 255, ${this.quantum_state * 0.1})`);
        gradient.addColorStop(1, 'rgba(0, 255, 255, 0)');
        this.ctx.fillStyle = gradient;
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
        
        this.dataPoints.forEach(point => {
            this.ctx.beginPath();
            const radius = 5 * (1 + Math.sin(time * this.phi + (point.phase || 0)) * 0.3);
            this.ctx.arc(point.x, point.y, radius, 0, Math.PI * 2);
            
            if(point.isAnomalous) {
                if(point.detected) {
                    this.ctx.fillStyle = `rgba(255, 0, 0, ${0.7 + Math.sin(time * 5) * 0.3})`;
                } else {
                    this.ctx.fillStyle = `rgba(255, 153, 0, ${0.7 + Math.sin(time * 3) * 0.3})`;
                }
            } else {
                this.ctx.fillStyle = `rgba(0, 255, 255, ${this.quantum_state})`;
            }
            
            this.ctx.fill();
            
            // Add quantum glow effect
            if(point.isAnomalous) {
                this.ctx.beginPath();
                this.ctx.arc(point.x, point.y, radius * 2, 0, Math.PI * 2);
                this.ctx.fillStyle = `rgba(255, ${point.detected ? 0 : 153}, 0, 0.1)`;
                this.ctx.fill();
            }
        });
        
        requestAnimationFrame(() => this.animate());
    }
}
