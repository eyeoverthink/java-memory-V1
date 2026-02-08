
import * as THREE from 'https://cdn.skypack.dev/three@0.132.2';
import { OrbitControls } from 'https://cdn.skypack.dev/three@0.132.2/examples/jsm/controls/OrbitControls.js';

class FRAYMUS3D {
    constructor() {
        this.PHI = (1 + Math.sqrt(5)) / 2;
        this.scene = new THREE.Scene();
        this.camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
        this.renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
        this.controls = null;
        this.quantumFields = [];
        this.protectionSpheres = [];
        this.particleSystems = [];
        this.init();
    }

    init() {
        this.renderer.setSize(window.innerWidth, window.innerHeight);
        document.getElementById('fraymus3d-container').appendChild(this.renderer.domElement);
        
        this.camera.position.set(5, 5, 5);
        this.controls = new OrbitControls(this.camera, this.renderer.domElement);
        
        this.createQuantumField();
        this.createProtectionSphere();
        this.createParticleSystem();
        this.animate();
        
        window.addEventListener('resize', () => this.onWindowResize());
    }

    createQuantumField() {
        const geometry = new THREE.IcosahedronGeometry(2, 3);
        this.securityLayer = new THREE.Mesh(
            new THREE.SphereGeometry(2.2, 32, 32),
            new THREE.MeshPhongMaterial({
                color: 0x00ffff,
                transparent: true,
                opacity: 0.1,
                wireframe: true
            })
        );
        this.scene.add(this.securityLayer);
        const material = new THREE.MeshPhongMaterial({
            color: 0x00ffff,
            wireframe: true,
            transparent: true,
            opacity: 0.3
        });
        
        const field = new THREE.Mesh(geometry, material);
        this.scene.add(field);
        this.quantumFields.push(field);

        const ambientLight = new THREE.AmbientLight(0x404040);
        this.scene.add(ambientLight);

        const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
        directionalLight.position.set(1, 1, 1);
        this.scene.add(directionalLight);
    }

    createProtectionSphere() {
        const geometry = new THREE.SphereGeometry(3, 32, 32);
        const material = new THREE.MeshPhongMaterial({
            color: 0x0066ff,
            transparent: true,
            opacity: 0.2,
            side: THREE.DoubleSide
        });
        
        const sphere = new THREE.Mesh(geometry, material);
        this.scene.add(sphere);
        this.protectionSpheres.push(sphere);
    }

    createParticleSystem() {
        const particleCount = 1000;
        const particles = new THREE.BufferGeometry();
        const positions = new Float32Array(particleCount * 3);

        for (let i = 0; i < particleCount * 3; i += 3) {
            positions[i] = (Math.random() - 0.5) * 10;
            positions[i + 1] = (Math.random() - 0.5) * 10;
            positions[i + 2] = (Math.random() - 0.5) * 10;
        }

        particles.setAttribute('position', new THREE.BufferAttribute(positions, 3));
        
        const material = new THREE.PointsMaterial({
            color: 0x00ffff,
            size: 0.05,
            transparent: true,
            opacity: 0.8
        });

        const particleSystem = new THREE.Points(particles, material);
        this.scene.add(particleSystem);
        this.particleSystems.push(particleSystem);
    }

    animate() {
        requestAnimationFrame(() => this.animate());
        
        const time = Date.now() * 0.001;

        this.quantumFields.forEach(field => {
            field.rotation.x += 0.001 * this.PHI;
            field.rotation.y += 0.002 * this.PHI;
            field.rotation.z += 0.003 * this.PHI;
        });

        this.protectionSpheres.forEach(sphere => {
            sphere.scale.x = 1 + Math.sin(time) * 0.1;
            sphere.scale.y = 1 + Math.cos(time) * 0.1;
            sphere.scale.z = 1 + Math.sin(time * 0.5) * 0.1;
        });

        this.particleSystems.forEach(particles => {
            particles.rotation.y += 0.001;
            const positions = particles.geometry.attributes.position.array;
            for (let i = 0; i < positions.length; i += 3) {
                positions[i + 1] += Math.sin(time + positions[i]) * 0.01;
            }
            particles.geometry.attributes.position.needsUpdate = true;
        });
        
        this.renderer.render(this.scene, this.camera);
    }

    onWindowResize() {
        this.camera.aspect = window.innerWidth / window.innerHeight;
        this.camera.updateProjectionMatrix();
        this.renderer.setSize(window.innerWidth, window.innerHeight);
    }
}

export default FRAYMUS3D;
