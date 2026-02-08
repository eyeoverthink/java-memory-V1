
// AI Warfare Integration
class AIWarfareSystem {
    constructor() {
        this.baseUrl = '/api';
        this.token = null;
    }

    async login() {
        try {
            const response = await fetch(`${this.baseUrl}/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    username: 'admin',
                    password: 'securepassword'
                })
            });
            const data = await response.json();
            this.token = data.access_token;
            return true;
        } catch (error) {
            console.error('Login failed:', error);
            return false;
        }
    }

    async solveCTFChallenge(challengeType) {
        if (!this.token) await this.login();
        try {
            const response = await fetch(`${this.baseUrl}/solve_ctf`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${this.token}`
                },
                body: JSON.stringify({ challenge_type: challengeType })
            });
            return await response.json();
        } catch (error) {
            console.error('CTF challenge failed:', error);
            return null;
        }
    }

    async scanTarget(target) {
        if (!this.token) await this.login();
        try {
            const response = await fetch(`${this.baseUrl}/security_scan`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${this.token}`
                },
                body: JSON.stringify({ target })
            });
            return await response.json();
        } catch (error) {
            console.error('Security scan failed:', error);
            return null;
        }
    }

    async crackHash(hash) {
        if (!this.token) await this.login();
        try {
            const response = await fetch(`${this.baseUrl}/crack_hash`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${this.token}`
                },
                body: JSON.stringify({ hash })
            });
            return await response.json();
        } catch (error) {
            console.error('Hash cracking failed:', error);
            return null;
        }
    }
}
