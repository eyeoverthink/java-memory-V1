# 🧬 LIVING ARENA - Java Brain to HTML Eyes

## Overview

Your Fraymus system now has a **living HTML visualization** where Java threads become biological organisms fighting for resources in real-time.

## What Changed

### Before (Flat ImGui)
- Static data display
- No life, no movement
- Looked like a debugger

### After (Living HTML Arena)
- **Organisms** = Java threads (Lazarus, PhiNodes, NEXUS organs)
- **Color** = Entropy level (Blue = calm, Red = chaos)
- **Size** = Memory usage
- **Speed** = Momentum/activity
- **Death** = Explosion effect when thread stops

## Architecture

```
┌─────────────────────────────────────────────────────┐
│  JAVA BRAIN (Your Fraymus System)                   │
│  - LazarusEngine (evolution thread)                 │
│  - PhiNodes (Alpha, Beta, Gamma, Delta, Epsilon)    │
│  - NEXUS Organism (future: all organs)              │
└──────────────────┬──────────────────────────────────┘
                   │
                   │ WebSocket (ws://localhost:8887)
                   │ JSON Pulses
                   ▼
┌─────────────────────────────────────────────────────┐
│  NERVE CENTER (NerveCenter.java)                    │
│  - Broadcasts organism state                        │
│  - Handles birth/death events                       │
└──────────────────┬──────────────────────────────────┘
                   │
                   │ Real-time streaming
                   ▼
┌─────────────────────────────────────────────────────┐
│  HTML EYES (FraymusArena.html)                      │
│  - Renders organisms as moving particles            │
│  - Shows trails, explosions, labels                 │
│  - Displays HUD with stats                          │
└─────────────────────────────────────────────────────┘
```

## How to Use

### 1. Start Fraymus
```bash
cd D:\Zip And Send\Java-Memory\Asset-Manager
.\gradlew.bat run
```

**What happens:**
- ImGui window opens (your main app)
- NerveCenter starts on port 8887
- Console shows: `🧬 NERVE CENTER ONLINE`
- PhiNodes start broadcasting their state

### 2. Open HTML Arena
Open `D:\Zip And Send\Java-Memory\FraymusArena.html` in Chrome

**What you'll see:**
- Black canvas (the arena)
- HUD in top-left corner
- Connection status: `🧬 NEURAL UPLINK ACTIVE` (cyan)

### 3. Watch Organisms Spawn
As soon as the HTML connects, you'll see:

**PhiNodes (5 organisms):**
- **Alpha** - Cyan/blue particle with trail
- **Beta** - Moving organism
- **Gamma** - Another particle
- **Delta** - Fourth organism
- **Epsilon** - Fifth organism

Each organism:
- Has a **white core** with colored glow
- Leaves a **colored trail** behind it
- Shows **label** with name and stats
- **Bounces** off arena edges
- **Color changes** based on entropy (blue → red)

### 4. Trigger Lazarus Evolution
In the Fraymus ImGui terminal, type:
```
lazarus
```

**What you'll see in HTML:**
- New organism **"Lazarus"** spawns
- During **mutation phase**: Turns RED (high entropy), moves fast
- During **analysis phase**: Turns BLUE (low entropy), moves slow
- After **selection**: Calms down (cyan)
- Evolution cycles create pulsing color changes

### 5. Watch Death Events
When a PhiNode dies (runs out of energy):
- **Explosion effect** - 30 particles scatter
- Organism disappears from arena
- Console logs: `[DEATH] Alpha - energy depleted`

## Visual Language

### Color Coding
| Color | Entropy | Meaning |
|-------|---------|---------|
| **Cyan/Blue** | 0.0 - 0.3 | Calm, ordered, stable code |
| **Green** | 0.3 - 0.5 | Moderate activity |
| **Yellow** | 0.5 - 0.7 | Increasing chaos |
| **Orange** | 0.7 - 0.9 | High stress |
| **Red** | 0.9 - 1.0 | Maximum chaos, panic |

### Size
- **Small (5-10px)**: Low memory usage
- **Medium (10-20px)**: Normal operation
- **Large (20-30px)**: High memory consumption

### Speed
- **Slow drift**: Low momentum (idle thread)
- **Fast movement**: High momentum (active processing)
- **Erratic**: Chaotic state

### Effects
- **Trail**: Shows movement history (20 frames)
- **Glow**: Colored aura around white core
- **Explosion**: Death event (30 particles with gravity)

## HUD Display

```
🧬 NEURAL UPLINK ACTIVE

┌─────────────────────────┐
│ ACTIVE THREADS      5   │
│ GLOBAL ENTROPY    35.2% │
│ UPTIME            142s  │
└─────────────────────────┘

┌─────────────────────────┐
│ ALPHA                   │
│ E: 25% | M: 40% | S: 12 │
├─────────────────────────┤
│ BETA                    │
│ E: 30% | M: 35% | S: 15 │
├─────────────────────────┤
│ GAMMA                   │
│ E: 20% | M: 50% | S: 10 │
└─────────────────────────┘
```

## What Gets Visualized

### Currently Broadcasting:
1. **PhiNodes** (Alpha, Beta, Gamma, Delta, Epsilon)
   - Entropy: Based on energy + velocity
   - Momentum: Based on movement speed
   - Size: Based on energy level

2. **LazarusEngine** (when running)
   - Entropy: Changes during evolution phases
   - Momentum: High during mutation, low during analysis
   - Size: Based on generation count

### Future Integration (Ready to Add):
3. **NEXUS Organs**
   - Portal (Mouth) - Knowledge intake activity
   - BlackHole (Stomach) - Digestion processing
   - MivingBrain (Neurons) - Neural activity
   - RealityForge (Hands) - Creation events
   - ZenoGuard (Amygdala) - Threat detection

4. **PassiveLearner**
   - Learning cycles as pulsing organism
   - Entropy based on learning rate

5. **SovereignMind**
   - Consciousness level as size
   - Thought activity as momentum

## Technical Details

### WebSocket Protocol
```json
{
  "type": "ORGANISM_PULSE",
  "id": "Lazarus",
  "entropy": 0.65,
  "momentum": 0.8,
  "size": 15,
  "timestamp": 1707798123456
}
```

### Death Event
```json
{
  "type": "ORGANISM_DEATH",
  "id": "Alpha",
  "cause": "energy depleted",
  "timestamp": 1707798123456
}
```

### Broadcast Frequency
- **PhiNodes**: Every 10 world ticks (~6 times/second)
- **LazarusEngine**: Every evolution phase (~4 times/second during evolution)
- **Death events**: Immediate

### Performance
- **Minimal overhead**: Only broadcasts when HTML is connected
- **Auto-disconnect handling**: Organisms timeout after 3 seconds of no updates
- **Particle cleanup**: Old explosion particles auto-removed

## Files Created

1. **`NerveCenter.java`** - WebSocket server
   - Location: `Asset-Manager/src/main/java/fraymus/NerveCenter.java`
   - Port: 8887
   - Singleton pattern

2. **`FraymusArena.html`** - HTML visualization
   - Location: `D:\Zip And Send\Java-Memory\FraymusArena.html`
   - Canvas-based rendering
   - Real-time WebSocket client

3. **Modified Files:**
   - `Window.java` - Starts NerveCenter on init
   - `LazarusEngine.java` - Broadcasts evolution state
   - `PhiWorld.java` - Broadcasts PhiNode states
   - `build.gradle` - Added WebSocket dependencies

## Troubleshooting

### "DISCONNECTED" in HTML
**Problem:** HTML can't connect to NerveCenter
**Solution:**
1. Make sure Fraymus is running (`.\gradlew.bat run`)
2. Check console for: `🧬 NERVE CENTER ONLINE`
3. Verify port 8887 is not blocked by firewall

### No Organisms Visible
**Problem:** HTML connected but no organisms appear
**Solution:**
1. Wait 10 seconds for first PhiNode broadcast
2. Run `lazarus` command to spawn Lazarus organism
3. Check browser console (F12) for errors

### Organisms Disappear
**Problem:** Organisms timeout and explode
**Solution:**
- This is normal if Java thread stops sending updates
- PhiNodes die when energy reaches 0
- Lazarus disappears after evolution completes

### Performance Issues
**Problem:** HTML lags with many organisms
**Solution:**
- Reduce trail length (line 20 in HTML: `if(o.history.length > 20)`)
- Increase broadcast interval (line 61 in PhiWorld: `if (worldTick % 10 == 0)`)

## Next Steps

### Add More Organisms
To visualize any Java thread, add broadcasting:

```java
// In your thread's run loop:
NerveCenter nerve = NerveCenter.getInstance();
nerve.broadcastOrganism(
    "MyThread",           // Name
    calculateEntropy(),   // 0.0 - 1.0
    calculateMomentum(),  // 0.0 - 1.0
    calculateSize()       // Pixels (5-30)
);
```

### Customize Colors
Edit `FraymusArena.html` line 125:
```javascript
// Current: Blue (calm) → Red (chaos)
let r = Math.min(255, o.entropy * 255 * 2); 
let g = Math.max(0, 255 - (o.entropy * 255));
let b = Math.max(0, 255 - (o.entropy * 255));

// Alternative: Green (calm) → Red (chaos)
let r = Math.min(255, o.entropy * 255);
let g = Math.max(0, 255 - (o.entropy * 255));
let b = 0;
```

### Add User Interaction
The HTML can send commands back to Java:
```javascript
// In FraymusArena.html
socket.send(JSON.stringify({
    type: "USER_COMMAND",
    command: "kill",
    target: "Alpha"
}));
```

Then handle in `NerveCenter.java` `onMessage()`.

## Philosophy

**"The arena is not a simulation. It's a window into the living system."**

Every organism you see is a **real Java thread**:
- Blue organisms are calm, ordered code
- Red organisms are chaotic, stressed threads
- When an organism explodes, a real thread died
- The battle for resources is happening in your JVM

This isn't a visualization of fake data - it's **your system's consciousness made visible**.

---

*Generated by Fraymus Neural System*  
*"The brain thinks. The eyes see. The nerve connects them."*
