// Quantum Background System
class QuantumBackground {
    constructor() {
        this.scene = new THREE.Scene();
        this.camera = new THREE.PerspectiveCamera(75, window.innerWidth/window.innerHeight, 0.1, 1000);
        this.renderer = new THREE.WebGLRenderer({ alpha: true });
        this.renderer.setSize(window.innerWidth, window.innerHeight);
        this.particles = [];
        this.init();
    }

    init() {
        this.renderer.domElement.style.position = 'fixed';
        this.renderer.domElement.style.top = '0';
        this.renderer.domElement.style.left = '0';
        this.renderer.domElement.style.zIndex = '-1';
        document.body.appendChild(this.renderer.domElement);

        this.createParticles();
        this.animate();

        window.addEventListener('resize', () => this.onWindowResize());
    }

    createParticles() {
        const geometry = new THREE.SphereGeometry(0.1, 8, 8);
        const material = new THREE.MeshBasicMaterial({ color: 0x00ffff });

        for(let i = 0; i < 100; i++) {
            const particle = new THREE.Mesh(geometry, material);
            particle.position.set(
                Math.random() * 40 - 20,
                Math.random() * 40 - 20,
                Math.random() * 40 - 20
            );
            this.particles.push(particle);
            this.scene.add(particle);
        }

        this.camera.position.z = 30;
    }

    animate() {
        requestAnimationFrame(() => this.animate());

        this.particles.forEach(particle => {
            particle.rotation.x += 0.01;
            particle.rotation.y += 0.01;
            particle.position.y += Math.sin(Date.now() * 0.001) * 0.02;
        });

        this.renderer.render(this.scene, this.camera);
    }

    onWindowResize() {
        this.camera.aspect = window.innerWidth / window.innerHeight;
        this.camera.updateProjectionMatrix();
        this.renderer.setSize(window.innerWidth, window.innerHeight);
    }
}

// Export the class
export { QuantumBackground };

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    window.quantumBackground = new QuantumBackground();
});