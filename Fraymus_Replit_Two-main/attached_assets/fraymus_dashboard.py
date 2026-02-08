
from flask import Flask, jsonify
import json

# Initialize Flask App
app = Flask(__name__)

# Load Cyberwarfare Data
with open("Fraymus_Cyberwarfare_Report.json", "r") as report_file:
    cyberwarfare_data = json.load(report_file)

@app.route('/api/cyberwarfare/summary', methods=['GET'])
def get_summary():
    """API Endpoint: Retrieve the Cyberwarfare Summary"""
    return jsonify(cyberwarfare_data["summary"]), 200

@app.route('/api/cyberwarfare/logs', methods=['GET'])
def get_logs():
    """API Endpoint: Retrieve the Full Cyberwarfare Logs"""
    return jsonify(cyberwarfare_data["detailed_log"]), 200

@app.route('/api/cyberwarfare/next_steps', methods=['GET'])
def get_next_steps():
    """API Endpoint: Retrieve Next Steps for Fraymus AI Deployment"""
    return jsonify(cyberwarfare_data["next_steps"]), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5100, debug=True)
