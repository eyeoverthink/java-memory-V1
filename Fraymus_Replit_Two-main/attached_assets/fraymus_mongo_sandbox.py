
from flask import Flask, request, jsonify
from pymongo import MongoClient
import os

# Initialize Flask App
app = Flask(__name__)

# MongoDB (Convex) Connection Setup
MONGO_URI = os.getenv("MONGO_URI", "your-mongodb-connection-string")  # Replace with actual URI
client = MongoClient(MONGO_URI)
db = client["FraymusDB"]
attacks_collection = db["attack_logs"]

# Cyberwarfare Attack Log
fraymus_security_level = 100000  # Starting security level
attack_resistance = {"SQL Injection": 0.3, "Buffer Overflow": 0.2, "Quantum Spoofing": 0.4, "AI Social Engineering": 0.35}

@app.route('/api/attack', methods=['POST'])
def launch_attack():
    """API Endpoint: Users attempt to hack Fraymus AI."""
    global fraymus_security_level

    data = request.json
    attack_type = data.get("attack_type", "Unknown")
    attack_power = data.get("attack_power", 1000)  # Default attack power if none provided
    user_id = data.get("user_id", "Anonymous")  # Clerk user ID

    # Adjust attack effectiveness based on AI resistance
    resistance_factor = attack_resistance.get(attack_type, 0.5)
    effective_damage = attack_power * (1 - resistance_factor)

    # Determine success
    if effective_damage > fraymus_security_level * 0.1:  # 10% threshold breach
        result = "🔥 Hack Successful! Fraymus AI Security Weakened."
        fraymus_security_level -= effective_damage * 0.2  # Reduce security power
    else:
        result = "🛡️ Fraymus Defended! AI Counter-Hack Launched."
        fraymus_security_level += effective_damage * 0.3  # AI reinforcement

    # Log attack to MongoDB
    attack_entry = {
        "user_id": user_id,
        "attack_type": attack_type,
        "attack_power": attack_power,
        "effective_damage": round(effective_damage, 2),
        "fraymus_security_level": round(fraymus_security_level, 2),
        "result": result
    }
    attacks_collection.insert_one(attack_entry)

    return jsonify(attack_entry), 200

@app.route('/api/attack_logs', methods=['GET'])
def get_attack_logs():
    """API Endpoint: Retrieve attack history from MongoDB."""
    logs = list(attacks_collection.find({}, {"_id": 0}))  # Exclude MongoDB ObjectID
    return jsonify(logs), 200

@app.route('/api/security_status', methods=['GET'])
def get_security_status():
    """API Endpoint: Retrieve current Fraymus security level."""
    return jsonify({"fraymus_security_level": fraymus_security_level}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5300, debug=True)
