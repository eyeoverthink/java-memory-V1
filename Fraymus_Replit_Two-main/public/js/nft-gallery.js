class NFTGallery {
    constructor() {
        this.PHI = (1 + Math.sqrt(5)) / 2;
        this.provider = null;
        this.signer = null;
        this.warriors = {
            'Loki': { power: 88, rarity: 'Mythic', image: '/attached_assets/Time_Warrior.png' },
            'Magnus': { power: 90, rarity: 'Legendary', image: '/attached_assets/Magnus_Warrior' },
            'Stopper': { power: 95, rarity: 'Ultra Rare', image: '/attached_assets/Crime_Stopper.png' },
            'Gold': { power: 99, rarity: 'Secret Rare', image: '/attached_assets/Electricity_Warrior.png' }
        };
        this.initialize();
    }

    async initialize() {
        const container = document.getElementById('nft-gallery');
        if (!container) return;

        if (typeof window.ethereum !== 'undefined') {
            try {
                this.provider = new ethers.providers.Web3Provider(window.ethereum);
                await window.ethereum.request({ method: 'eth_requestAccounts' });
                this.signer = this.provider.getSigner();
            } catch (error) {
                console.log('Wallet connection deferred - running in preview mode');
            }
        }

        this.renderGallery();
    }

    renderGallery() {
        const container = document.getElementById('nft-gallery');
        if (!container) return;

        container.innerHTML = this.generateNFTCards();
        this.initializeCanvases();
    }

    generateNFTCards() {
        return Object.entries(this.warriors)
            .map(([name, data], i) => `
                <div class="nft-card ${data.rarity.toLowerCase().replace(' ', '-')}">
                    <div class="nft-image">
                        <img src="${data.image}" alt="${name}" onerror="this.src='/attached_assets/default_warrior.png'"/>
                    </div>
                    <div class="warrior-info">
                        <h4>FRAYMUS ${name}</h4>
                        <p>Power: φ${(data.power * this.PHI).toFixed(2)}</p>
                        <div class="rarity-badge ${data.rarity.toLowerCase()}">${data.rarity}</div>
                    </div>
                </div>
            `).join('');
    }

    initializeCanvases() {
        // Add quantum effects to NFT cards
        document.querySelectorAll('.nft-card').forEach(card => {
            card.addEventListener('mouseover', () => {
                card.style.transform = 'scale(1.05)';
                card.style.boxShadow = '0 0 20px rgba(0, 255, 255, 0.5)';
            });

            card.addEventListener('mouseout', () => {
                card.style.transform = 'scale(1)';
                card.style.boxShadow = 'none';
            });
        });
    }
}

// Initialize gallery when document is ready
document.addEventListener('DOMContentLoaded', () => {
    window.nftGallery = new NFTGallery();
});