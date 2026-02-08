class SecurityCore {
    constructor() {
        this.provider = null;
        this.signer = null;
        this.initializeSecurity();
    }

    initializeSecurity() {
        if (window.ethereum) {
            this.provider = new ethers.providers.Web3Provider(window.ethereum);
        }
    }

    async verifyAccess(params) {
        if (!window.ethereum) {
            alert('Please install a Web3 wallet like MetaMask');
            return;
        }
        
        try {
            const accounts = await window.ethereum.request({ 
                method: 'eth_requestAccounts' 
            });
            
            this.provider = new ethers.providers.Web3Provider(window.ethereum);
            this.signer = this.provider.getSigner();
            
            return {
                success: true,
                address: accounts[0],
                timestamp: Date.now()
            };
        } catch (error) {
            console.error('Wallet connection error:', error);
            throw error;
        }
    }
}

// Initialize wallet connection
document.addEventListener('DOMContentLoaded', () => {
    // Initialize FRAYMUS agent
    window.fraymusAgent = new FraymusAgent();

    // Add click handler for agent tab
    const agentTab = document.querySelector('[data-tab="agent"]');
    if (agentTab) {
        agentTab.addEventListener('click', () => {
            document.querySelectorAll('.panel').forEach(p => p.style.display = 'none');
            document.querySelector('.agent-panel').style.display = 'block';
        });
    }

    // Initialize Quantum Effects
    window.quantumEffects = new QuantumEffects();

    // Initialize tabs
    const tabs = document.querySelectorAll('.tab-btn');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            document.querySelectorAll('.tab-btn').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
        });
    });

    if (!document.querySelector('.container')) {
        return;
    }

    const security = new SecurityCore();
    if (window.quantumEffects) {
        window.quantumEffects.init();
    }

    // Initialize UI elements safely with null checks
    const initializeElements = () => {
        const tabs = document.querySelectorAll('.tab-btn');
        if (tabs) {
            tabs.forEach(tab => {
                tab.addEventListener('click', () => {
                    const panelClass = tab.dataset.tab;
                    if (!panelClass) return;

                    const targetPanel = document.querySelector(`.${panelClass}-panel`) ||
                                              document.querySelector(`.${panelClass}-grid`);
                    if (targetPanel) {
                        document.querySelectorAll('.test-panel, .logic-panel, .simulation-grid, .sandbox-panel, .nft-panel, .agent-panel')
                            .forEach(panel => {
                                if (panel) panel.style.display = 'none';
                            });
                        targetPanel.style.display = 'block';
                    }
                });
            });
        }

        // Safe element initialization
        const safeSetHandler = (elementId, handler) => {
            const element = document.getElementById(elementId);
            if (element) {
                element.addEventListener('input', handler);
            }
        };

        // Initialize input handlers
        safeSetHandler('qfp-input', (e) => {
            const result = security.generateQuantumSeal(e.target.value, Date.now());
            const qfpResult = document.getElementById('qfp-result');
            if (qfpResult) {
                qfpResult.textContent = result.signature;
            }
        });

        // Initialize wallet connection (updated)
        const connectWallet = document.getElementById('connect-wallet');
        if (connectWallet) {
            connectWallet.addEventListener('click', async () => {
                try {
                    const result = await security.verifyAccess({
                        action: 'connect',
                        timestamp: Date.now()
                    });
                    const walletAddress = document.getElementById('wallet-address');
                    if (walletAddress) {
                        walletAddress.textContent = `${result.address.substring(0, 6)}...${result.address.substring(38)}`;
                    }
                    document.getElementById('wallet-info').style.display = 'block';
                } catch (error) {
                    console.error('Wallet connection error:', error);
                }
            });
        }
    };

    initializeElements();
});

window.SecurityCore = SecurityCore;