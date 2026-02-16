# Quantum Oracle Dependencies

## Core Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Flask | 3.0.0 | Web framework for API endpoints |
| Flask-PyMongo | 2.3.0 | MongoDB integration for Flask |
| pymongo | 4.6.1 | MongoDB driver for Python |
| Flask-JWT-Extended | 4.6.0 | JWT authentication for Flask |
| Flask-Cors | 4.0.0 | Cross-Origin Resource Sharing for Flask |
| python-dotenv | 1.0.0 | Environment variable management |
| requests | 2.31.0 | HTTP library for API calls |
| pytest | 7.4.3 | Testing framework |
| streamlit | 1.32.0 | Web app framework for the main interface |
| plotly | 5.18.0 | Interactive visualization library |
| numpy | 1.26.3 | Numerical computing library |
| asyncio | 3.4.3 | Asynchronous I/O library |

## Additional Dependencies

| Dependency | Purpose |
|------------|---------|
| nest_asyncio | Fixes for asyncio and PyTorch issues |
| torch | Deep learning framework (optional) |
| matplotlib | Visualization library |
| PIL (Pillow) | Image processing library |
| tqdm | Progress bar for long-running operations |
| scipy | Scientific computing library |
| cv2 (OpenCV) | Computer vision library |
| sqlite3 | SQLite database for language lessons |
| cryptography | Encryption for secure data storage |
| BeautifulSoup | HTML parsing for web scraping |

## File Dependencies

| File | Dependencies |
|------|--------------|
| quantum_oracle_app.py | streamlit, matplotlib, plotly, PIL, quantum_chat.py, fraymus_agent.py, multi_brain_quantum_sync.py, meta_storage.py, passive_learning.py |
| quantum_chat.py | streamlit, numpy, plotly, fraymus_agent.py |
| fraymus_agent.py | numpy, scipy, cv2, sqlite3, cryptography, flask |
| multi_brain_quantum_sync.py | numpy, tqdm |
| quantum_language.py | random, sqlite3, hashlib, json |
| quantum_cli.py | argparse, asyncio, quantum_language.py |
| meta_storage.py | os, json, datetime |
| passive_learning.py | numpy, threading, pickle, quantum_typing.py |
| quantum_typing.py | typing |

## Optional Components

Some components are imported with fallbacks if not available:
- quantum_neural_cortex.py
- tesla_phi_resonance.py
- neural_tesla_integration.py
- tesla_tachy_brain.py

These are referenced in multi_brain_quantum_sync.py but have dummy implementations if not available.
