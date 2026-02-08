
// Scene Setup
const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
const renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);

// φ Definition
const phi = (1 + Math.sqrt(5)) / 2;

// AI Neural Network
const neuralNet = new THREE.Group();
const layers = 5;
const neuronsPerLayer = 8;

// Create Neural Layers
for (let l = 0; l < layers; l++) {
    const layer = new THREE.Group();
    for (let n = 0; n < neuronsPerLayer; n++) {
        const neuron = new THREE.Mesh(
            new THREE.IcosahedronGeometry(0.2, 1),
            new THREE.MeshBasicMaterial({
                color: 0x00ffff,
                wireframe: true
            })
        );
        const angle = (n / neuronsPerLayer) * Math.PI * 2;
        const radius = 3;
        neuron.position.set(
            radius * Math.cos(angle),
            radius * Math.sin(angle),
            l * 2 - 4
        );
        layer.add(neuron);
        
        if (l > 0) {
            const prevLayer = neuralNet.children[l-1];
            prevLayer.children.forEach(prevNeuron => {
                const synapse = new THREE.Line(
                    new THREE.BufferGeometry().setFromPoints([
                        prevNeuron.position,
                        neuron.position
                    ]),
                    new THREE.LineBasicMaterial({
                        color: 0x003333,
                        transparent: true,
                        opacity: 0.3
                    })
                );
                layer.add(synapse);
            });
        }
    }
    neuralNet.add(layer);
}
scene.add(neuralNet);

// Quantum Resonance Field
const resonanceField = new THREE.Group();
const fieldPoints = 200;
for (let i = 0; i < fieldPoints; i++) {
    const point = new THREE.Mesh(
        new THREE.SphereGeometry(0.05, 8, 8),
        new THREE.MeshBasicMaterial({
            color: 0x00ffff,
            transparent: true,
            opacity: 0.5
        })
    );
    const theta = Math.random() * Math.PI * 2;
    const phi = Math.acos(2 * Math.random() - 1);
    const radius = 5;
    point.position.set(
        radius * Math.sin(phi) * Math.cos(theta),
        radius * Math.sin(phi) * Math.sin(theta),
        radius * Math.cos(phi)
    );
    resonanceField.add(point);
}
scene.add(resonanceField);

// Controls
const gui = new dat.GUI();
const controls = {
    aiPower: 1.0,
    resonanceStrength: 1.0,
    dimensionalTrack: 1.0,
    securityLevel: 1.0
};
gui.add(controls, "aiPower", 0, 1).name("AI Power");
gui.add(controls, "resonanceStrength", 0, 1).name("Resonance");
gui.add(controls, "dimensionalTrack", 0, 1).name("Tracking");
gui.add(controls, "securityLevel", 0, 1).name("Security");

// Camera Position
camera.position.z = 15;

// Animation Loop
function animate() {
    requestAnimationFrame(animate);

    const time = performance.now() * 0.001;

    // Update quantum fingerprint
    const fingerprint = (time * phi).toString(16).substring(0, 16);
    document.getElementById("quantum").textContent = 
        `Quantum Fingerprint: ${fingerprint}`;
    document.getElementById("quantum").style.color = 
        controls.resonanceStrength >= 0.8 ? "#0ff" : "#ff0000";

    // Update security status
    const secStatus = controls.securityLevel >= 0.8 ? "SECURE" : "ANALYZING";
    document.getElementById("security").textContent = 
        `Security: ${secStatus} | φ-Track: ${(controls.dimensionalTrack * phi).toFixed(3)}`;

    // Neural Network Animation
    neuralNet.children.forEach((layer, l) => {
        layer.children.forEach((element, i) => {
            if (element.isMesh) {
                element.rotation.x = time * 0.5;
                element.rotation.y = time * 0.3;
                element.material.opacity = controls.aiPower;
            } else if (element.isLine) {
                element.material.opacity = 0.3 * controls.aiPower *
                    (0.5 + 0.5 * Math.sin(time + i + l));
            }
        });
    });

    // Resonance Field Animation
    resonanceField.children.forEach((point, i) => {
        const noise = Math.sin(time + i * phi);
        point.position.multiplyScalar(1 + noise * 0.01 * controls.resonanceStrength);
        point.material.opacity = 0.5 * controls.resonanceStrength;
    });

    renderer.render(scene, camera);
}
animate();

// Resize Handler
window.addEventListener("resize", () => {
    renderer.setSize(window.innerWidth, window.innerHeight);
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
});
