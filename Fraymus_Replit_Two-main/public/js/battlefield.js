class QuantumBattlefield {
    constructor() {
        this.scene = new THREE.Scene();
        this.container = document.getElementById('battlefield-container');
        this.threatLevel = 0;
        this.warriors = new Map();
        this.enemies = new Map();
        this.camera = new THREE.PerspectiveCamera(75, this.container.clientWidth / this.container.clientHeight, 0.1, 1000);
        this.renderer = new THREE.WebGLRenderer({ antialias: true });
        this.renderer.setSize(this.container.clientWidth, this.container.clientHeight);
        this.container.appendChild(this.renderer.domElement);

        this.camera.position.z = 5;
        this.camera.position.y = 2;
        this.camera.lookAt(0, 0, 0);

        // Add lighting
        const ambientLight = new THREE.AmbientLight(0x404040);
        const pointLight = new THREE.PointLight(0x00ffff, 1, 100);
        pointLight.position.set(5, 5, 5);
        this.scene.add(ambientLight, pointLight);

        // Create grid
        const gridHelper = new THREE.GridHelper(10, 10, 0x00ffff, 0x004444);
        this.scene.add(gridHelper);

        // Create quantum particles
        this.particles = new THREE.Group();
        this.createParticles();
        this.scene.add(this.particles);

        // Add φ-space grid (inspired by the changes)
        const phiSpaceGrid = new THREE.GridHelper(20, 20, 0x8888ff, 0x444488); // Added distinct color
        phiSpaceGrid.position.set(5, 0, 5); // Offset for better visibility
        this.scene.add(phiSpaceGrid);

        // Animation
        this.animate();

        // Handle window resize
        window.addEventListener('resize', () => this.onWindowResize());
    }

    createParticles() {
        const geometry = new THREE.SphereGeometry(0.1, 8, 8);
        const material = new THREE.MeshPhongMaterial({
            color: 0x00ffff,
            transparent: true,
            opacity: 0.7
        });

        for (let i = 0; i < 20; i++) {
            const particle = new THREE.Mesh(geometry, material);
            particle.position.set(
                (Math.random() - 0.5) * 8,
                (Math.random() - 0.5) * 4,
                (Math.random() - 0.5) * 8
            );
            particle.userData = {
                velocity: new THREE.Vector3(
                    (Math.random() - 0.5) * 0.02,
                    (Math.random() - 0.5) * 0.02,
                    (Math.random() - 0.5) * 0.02
                )
            };
            this.particles.add(particle);
        }
    }

    updateParticles() {
        this.particles.children.forEach(particle => {
            particle.position.add(particle.userData.velocity);

            // Boundary check and bounce
            ['x', 'y', 'z'].forEach(axis => {
                if (Math.abs(particle.position[axis]) > 4) {
                    particle.userData.velocity[axis] *= -1;
                }
            });
        });
    }

    onWindowResize() {
        this.camera.aspect = this.container.clientWidth / this.container.clientHeight;
        this.camera.updateProjectionMatrix();
        this.renderer.setSize(this.container.clientWidth, this.container.clientHeight);
    }

    animate() {
        requestAnimationFrame(() => this.animate());

        // Spawn threats with increasing frequency based on score
        const spawnRate = 0.005 * (1 + this.score / 1000);
        if (Math.random() < spawnRate) {
            this.spawnThreat();
        }

        // Update threats with more dynamic movement
        this.enemies.forEach((threat, id) => {
            const age = Date.now() - id;
            threat.rotation.y += 0.02 * (1 + age/5000);
            threat.position.y = 1 + Math.sin(age * 0.002) * 0.3;
            threat.position.x += Math.sin(age * 0.001) * 0.01;

            // Threat power increases with age
            threat.scale.setScalar(1 + age/10000);

            if (age > 10000) {
                this.scene.remove(threat);
                this.enemies.delete(id);
            }
        });

        // Update warriors with level progression
        this.warriors.forEach(warrior => {
            const level = warrior.userData.level || 1;
            warrior.rotation.y += 0.01 * level;

            // Level up based on threat elimination
            if (Math.random() < 0.005 * level) {
                this.updateWarriorLevel(warrior.id, level + 1);
                warrior.scale.setScalar(1 + level * 0.1);
                warrior.material.emissiveIntensity = 0.2 * level;
            }

            // Add power aura
            const aura = new THREE.PointLight(0x00ffff, 0.5 * level, 100);
            warrior.add(aura);
        });

        this.updateParticles(); // Update particle positions in animation loop
        this.renderer.render(this.scene, this.camera);
    }

    triggerAttack(type) {
        // Add visual effects for different attack types
        switch(type) {
            case 'SQL Injection':
                this.createAttackWave(0xff0000, 'wave');
                break;
            case 'Buffer Overflow': 
                this.createAttackWave(0xff8800, 'burst');
                break;
            case 'Quantum Spoofing':
                this.createAttackWave(0x00ffff, 'quantum');
                break;
            case 'AI Social Engineering':
                this.createAttackWave(0x00ff00, 'neural');
                break;
        }
    }

    createAttackWave(color, pattern) {
        const geometry = new THREE.SphereGeometry(0.1, 8, 8);
        const adversaryColors = {
            'AI Infiltrator': 0xff0000,
            'Quantum Hacker': 0x00ff00,
            'Cyber Warrior': 0x0000ff
        };
        const material = new THREE.MeshPhongMaterial({
            color: adversaryColors[this.currentAdversary?.name] || color,
            transparent: true,
            opacity: 0.8,
            emissive: adversaryColors[this.currentAdversary?.name] || color,
            emissiveIntensity: 0.5
        });

        const phi = (1 + Math.sqrt(5)) / 2;
        const quantumScale = Math.pow(phi, 2);

        const wave = new THREE.Mesh(geometry, material);
        wave.position.set(0, 0, 0);

        this.scene.add(wave);

        const animate = (time) => {
            wave.scale.set(1 + time/500, 1 + time/500, 1 + time/500);
            wave.material.opacity = Math.max(0, 0.8 - time/1000);

            if(wave.material.opacity > 0) {
                requestAnimationFrame(animate);
            } else {
                this.scene.remove(wave);
                wave.geometry.dispose();
                wave.material.dispose();
            }
        };

        requestAnimationFrame(animate);
    }

    createWarrior(id, level, position) {
        const geometry = new THREE.SphereGeometry(0.2, 16, 16);
        const material = new THREE.MeshPhongMaterial({
            color: 0x00ffaa,
            emissive: 0x006622,
            transparent: true,
            opacity: 0.8
        });

        const warrior = new THREE.Mesh(geometry, material);
        warrior.position.set(...position);
        warrior.userData = {
            level,
            power: Math.pow(1.618, level), 
            experience: 0
        };

        const levelRing = this.createLevelIndicator(level);
        warrior.add(levelRing);

        this.warriors.set(id, warrior);
        this.scene.add(warrior);
        return warrior;
    }

    createLevelIndicator(level) {
        const ring = new THREE.Mesh(
            new THREE.TorusGeometry(0.3, 0.03, 8, 32),
            new THREE.MeshPhongMaterial({
                color: this.getLevelColor(level),
                transparent: true,
                opacity: 0.6
            })
        );
        ring.rotation.x = Math.PI / 2;
        return ring;
    }

    getLevelColor(level) {
        if(level < 3) return 0x00ff00;      // Green
        if(level < 5) return 0x00ffff;      // Cyan
        if(level < 7) return 0x0088ff;      // Blue
        if(level < 9) return 0xff00ff;      // Purple
        return 0xffff00;                    // Gold
    }

    createThreat(level, position) {
        const geometry = new THREE.OctahedronGeometry(0.2 * level);
        const material = new THREE.MeshPhongMaterial({
            color: 0xff0000,
            emissive: 0x660000,
            transparent: true,
            opacity: 0.7
        });

        const threat = new THREE.Mesh(geometry, material);
        threat.position.set(...position);
        threat.userData = {
            level,
            power: Math.pow(2, level),
            type: ['Virus', 'Trojan', 'Ransomware', 'Zero-Day'][Math.floor(Math.random() * 4)]
        };

        const threatAura = this.createThreatAura(level);
        threat.add(threatAura);

        return threat;
    }


    createThreatAura(level) {
        const geometry = new THREE.SphereGeometry(0.4 * level, 16, 16);
        const material = new THREE.MeshBasicMaterial({
            color: 0xff0000,
            transparent: true,
            opacity: 0.2
        });
        return new THREE.Mesh(geometry, material);
    }

    updateWarriorLevel(id, newLevel) {
        const warrior = this.warriors.get(id);
        if (warrior) {
            warrior.userData.level = newLevel;
            warrior.userData.power = Math.pow(1.618, newLevel);
            warrior.scale.setScalar(1 + newLevel * 0.1);

            // Update level indicator
            warrior.children[0].scale.setScalar(1 + newLevel * 0.1);
        }
    }

    spawnThreat() {
        const position = [
            (Math.random() - 0.5) * 8,
            1 + Math.random() * 2,
            (Math.random() - 0.5) * 8
        ];
        const threat = this.createThreat(1 + Math.floor(Math.random() * 3), position);
        this.scene.add(threat);
        this.enemies.set(Date.now(), threat);
    }
}

// Initialize battlefield when document is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.battlefield = new QuantumBattlefield();
});