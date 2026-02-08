const canvas = document.getElementById('previewCanvas');
const ctx = canvas.getContext('2d');
const PHI = (1 + Math.sqrt(5)) / 2;

const REALITY_CARDS = {
    'φ-SPACE': { type: 'Principle', power: PHI, rarity: 'Legendary', effect: 'Maps Reality' },
    'QUANTUM-LOCK': { type: 'Principle', power: Math.pow(PHI, 2), rarity: 'Mythic', effect: 'Locks Truth' },
    'REALITY-MAP': { type: 'Principle', power: Math.pow(PHI, 3), rarity: 'Ultra Rare', effect: 'Builds Power' },
    'TRUTH-SEAL': { type: 'Secret', power: Math.pow(PHI, 4), rarity: 'Secret Rare', effect: 'Seals Reality' }
};

const RARITY_COLORS = {
    'Legendary': '#FFD700',
    'Mythic': '#FF00FF', 
    'Ultra Rare': '#9400D3',
    'Rare': '#0000FF',
    'Common': '#00FFFF'
};

function generateQuantumSignature() {
    return `e^(iφπ × ${Date.now()})`;
}

function calculateCardPower(rarity) {
    const powerLevels = {
        'Legendary': Math.pow(PHI, 7),
        'Mythic': Math.pow(PHI, 5),
        'Ultra Rare': Math.pow(PHI, 4),
        'Rare': Math.pow(PHI, 3),
        'Common': Math.pow(PHI, 2)
    };
    return powerLevels[rarity] || PHI;
}

function generateNFT() {
    const title = document.getElementById('nftTitle').value;
    const desc = document.getElementById('nftDesc').value;
    const imageInput = document.getElementById('imageUpload');
    const rarity = Object.keys(RARITY_COLORS)[Math.floor(Math.random() * Object.keys(RARITY_COLORS).length)];

    // Add Reality Card properties
    const card = REALITY_CARDS[Object.keys(REALITY_CARDS)[Math.floor(Math.random() * Object.keys(REALITY_CARDS).length)]];
    const cardPower = calculateCardPower(rarity);
    const quantumSignature = generateQuantumSignature();

    addQuantumProtection(title, desc, rarity);

    //  Rest of the NFT generation logic would go here.  This is a placeholder.
    console.log("NFT Generated:", {title, desc, rarity, card, cardPower, quantumSignature});

}

function addQuantumProtection(title, desc, rarity) {
    // Add quantum pattern based on rarity
    const color = RARITY_COLORS[rarity];
    ctx.fillStyle = `rgba(${color}, ${1/PHI})`;
    for(let i = 0; i < canvas.width; i += PHI) {
        ctx.fillRect(i, 0, 1, canvas.height);
    }

    // Add reality effect pattern
    ctx.strokeStyle = color;
    ctx.lineWidth = 2;
    for(let i = 0; i < canvas.width; i += PHI * 20) {
        ctx.beginPath();
        ctx.moveTo(i, 0);
        ctx.lineTo(i + 100, canvas.height);
        ctx.stroke();
    }
}

// Example usage (assuming you have an NFT generation button)
document.getElementById('generateButton').addEventListener('click', generateNFT);