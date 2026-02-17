# FRAYNIX OS Quick Start Guide

## ⚠️ Issue Fixed: WebSocket Connection

**Problem:** The WebSocket connection was failing with "404 WebSocket Upgrade Failure" because:
1. The HTML was trying to connect to `ws://localhost:8082/fraynix` (with path)
2. Java WebSocketServer doesn't use paths by default
3. FraymusConvergence can't run via `gradlew runConvergence` (non-interactive stdin)

**Solution:** 
- ✅ Fixed WebSocket URL to `ws://localhost:8082` (no path)
- ✅ Created interactive launcher scripts

---

## 🚀 How to Start FRAYNIX OS (2 Options)

### Option 1: Manual Start (Recommended)

**Step 1: Build the project**
```powershell
cd "D:\Zip And Send\Java-Memory\Asset-Manager"
.\gradlew.bat build -x test
```

**Step 2: Start FraymusConvergence in interactive PowerShell**
```powershell
java -cp "build/libs/Asset-Manager.jar;build/install/Asset-Manager/lib/*" fraymus.FraymusConvergence
```

**Step 3: Wait for startup (you'll see):**
```
✓ FRAYNIX OS WebSocket ready (port 8082)
✓ HTTP server started on port 8083
Type 'help' for commands
CONVERGENCE_01>
```

**Step 4: Launch visualization**
```
CONVERGENCE_01> visualize
```

Browser opens to FRAYNIX OS interface!

---

### Option 2: Batch Script (Windows)

**Step 1: Run the launcher**
```powershell
cd "D:\Zip And Send\Java-Memory\Asset-Manager"
.\start-fraynix.bat
```

**Step 2: In another terminal, open the visualization**
```powershell
.\open-fraynix.bat
```

Or manually navigate to: http://localhost:8083/fraynix-os.html

---

## 🧪 Testing the Integration

Once both are running:

### Test 1: HDC Prediction → Synaptic Jump
```
CONVERGENCE_01> learn The golden ratio phi is 1.618
CONVERGENCE_01> predict The golden
```
→ **Watch the 4D tesseract brain show a synaptic jump!**

### Test 2: Genesis Architect
```
CONVERGENCE_01> genesis create calculator
```
→ **LLM generates code + geometric app spawns in 3D**

### Test 3: DreamState
```
CONVERGENCE_01> dreamstate enter
```
→ **Visualization shifts to purple, pulse slows to 2Hz**

```
CONVERGENCE_01> dreamstate wake
```
→ **Returns to cyan, 10Hz pulse**

---

## 🔧 Troubleshooting

### "Address already in use: bind"
**Solution:** Kill existing Java processes
```powershell
taskkill /F /IM java.exe
```

### "404 WebSocket Upgrade Failure"
**Solution:** Already fixed! Make sure you're using the updated `fraynix-os.html` with:
```javascript
ws = new WebSocket('ws://localhost:8082');  // No /fraynix path
```

### WebSocket won't connect
**Check:**
1. Is FraymusConvergence running? (You should see the prompt `CONVERGENCE_01>`)
2. Did you see "✓ FRAYNIX OS WebSocket ready (port 8082)"?
3. Open browser console (F12) and check for connection errors

### HTTP server not serving HTML
**Solution:** Make sure `fraynix-os.html` is in the `Asset-Manager` directory (not in subdirectories)

---

## 📊 What You Should See

### Terminal Output:
```
✓ FraynixWebSocket initialized on port 8082
✓ HTTP server started on port 8083
✓ FRAYNIX OS WebSocket ready (port 8082)

Type 'help' for commands

CONVERGENCE_01> 
```

### Browser (FRAYNIX OS):
- 4D tesseract brain rotating
- 3,000 NanoSwarm agents orbiting
- System metrics (consciousness: 10.0%, agents: 2048)
- FrayShell terminal at bottom
- "WebSocket connected to backend" in green

### Browser Console (F12):
```
🔗 Connected to FraymusConvergence
```

---

## 🎯 Architecture

```
FraymusConvergence (Java)
    ↓ Port 8082
WebSocket Server
    ↓ ws://localhost:8082
fraynix-os.html (Browser)
    ↓ HTTP Port 8083
Three.js Visualization
```

---

## ✅ Verification Checklist

- [ ] FraymusConvergence starts without errors
- [ ] See "✓ FRAYNIX OS WebSocket ready (port 8082)"
- [ ] Browser opens to FRAYNIX OS interface
- [ ] WebSocket shows "connected" in green
- [ ] 4D tesseract is rotating
- [ ] NanoSwarm agents are orbiting
- [ ] `predict` command triggers synaptic jump
- [ ] `genesis` command generates code
- [ ] `dreamstate` changes theme to purple

---

## 📁 Files Changed

**Fixed:**
- `fraynix-os.html` - WebSocket URL changed to `ws://localhost:8082`

**Created:**
- `start-fraynix.bat` - Interactive launcher
- `open-fraynix.bat` - Opens visualization in browser
- `FRAYNIX_QUICKSTART.md` - This guide

---

## 🚀 Next Steps

1. **Start FraymusConvergence** using one of the methods above
2. **Open the visualization** (via `visualize` command or batch script)
3. **Test the integration** with the commands listed above
4. **Enjoy the AGI OS!** 🎉

The WebSocket connection issue is now fixed. The system should work perfectly! 🧠✨
