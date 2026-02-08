
class Web3Service {
    constructor() {
        this.web3 = null;
        this.account = null;
        this.chainId = null;
        this.PHI = (1 + Math.sqrt(5)) / 2;
    }

    async initialize() {
        if (typeof window.ethereum !== 'undefined') {
            try {
                this.web3 = new Web3(window.ethereum);
                const accounts = await window.ethereum.request({ method: 'eth_requestAccounts' });
                this.account = accounts[0];
                this.chainId = await window.ethereum.request({ method: 'eth_chainId' });
                return true;
            } catch (error) {
                console.error('Web3 initialization error:', error);
                return false;
            }
        }
        return false;
    }

    async signMessage(message) {
        try {
            const signature = await this.web3.eth.personal.sign(message, this.account);
            return signature;
        } catch (error) {
            console.error('Signing error:', error);
            return null;
        }
    }

    generateQuantumHash(data) {
        const phiComponent = Math.pow(this.PHI, 7.5);
        return this.web3.utils.keccak256(
            this.web3.utils.encodePacked(
                {type: 'uint256', value: phiComponent.toString()},
                {type: 'string', value: JSON.stringify(data)}
            )
        );
    }
}
