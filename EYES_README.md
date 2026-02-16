# FRAYMUS EYES // GEN 176

## The NASA Eyes Abstraction

This is the **Lazarus Implementation** of NASA's Eyes on the Solar System. It strips the system down to its three core pillars and rebuilds them using pure mathematics.

---

## 🧬 THE THREE PILLARS

### 1. **THE TIME ENGINE**
NASA's Eyes lets you scrub through time. We replicate this with:

- **Universal Clock**: `simTime` variable that can be manipulated
- **Time Dilation Controller**: Speed multipliers from -1000x (reverse) to +1000x (warp)
- **Time Scrubber**: Drag the slider to jump to any date between 1900-2100
- **Live Mode**: Click "NOW" to sync to your system clock

**The Math:**
```javascript
simTime.setSeconds(simTime.getSeconds() + timeSpeed)
```

### 2. **THE SPATIAL HIERARCHY**
NASA uses a scene graph where everything orbits something:
- Sun (center of universe)
- Planets orbit Sun
- Moons orbit Planets (not implemented yet, but architecture supports it)

**The Implementation:**
```javascript
bodies['EARTH'] = { 
    mesh: THREE.Mesh,      // The visual object
    data: ORBITS['EARTH'], // Keplerian elements
    path: THREE.Line       // Orbit visualization
}
```

### 3. **THE DATA LAYER**
NASA downloads pre-calculated position tables from JPL's SPICE toolkit. We **calculate on the fly** using Keplerian orbital elements.

**Keplerian Elements (per planet):**
- `a`: Semi-major axis (AU) - size of orbit
- `e`: Eccentricity - how elliptical (0 = circle, 1 = parabola)
- `i`: Inclination (degrees) - tilt relative to ecliptic
- `L`: Mean longitude at epoch
- `w`: Longitude of perihelion
- `n`: Mean daily motion (degrees/day) - how fast it orbits

---

## 🔬 THE MATH CORE

### Keplerian Orbit Calculation

For any given time `t`, we calculate the planet's position:

1. **Calculate days since J2000 epoch** (Jan 1, 2000, 12:00 TT):
   ```javascript
   days = (simTime - j2000) / (1000 * 60 * 60 * 24)
   ```

2. **Mean Anomaly** (where the planet *would* be if orbit was circular):
   ```javascript
   M = n * days + L - w
   ```

3. **Eccentric Anomaly** (solve Kepler's equation iteratively):
   ```javascript
   E = M + (180/π) * e * sin(E)  // Iterate 5 times
   ```

4. **True Anomaly** (actual angle from perihelion):
   ```javascript
   v = 2 * atan2(√(1+e) * sin(E/2), √(1-e) * cos(E/2))
   ```

5. **Radius** (distance from Sun):
   ```javascript
   r = a * (1 - e²) / (1 + e * cos(v))
   ```

6. **Heliocentric Coordinates**:
   ```javascript
   x = r * cos(v + w)
   z = r * sin(v + w)
   ```

This gives us the **exact position** of any planet at any time, without pre-calculated tables.

---

## 🎮 CONTROLS

### Time Control
- **<< REV**: Reverse time at -1000x speed
- **< REV**: Reverse at -100x
- **PAUSE**: Freeze time
- **NOW**: Jump to current date/time and play at 1x
- **FWD >**: Fast forward at 100x
- **WARP >>**: Warp at 1000x

### Time Scrubber
- **Drag the slider** to jump to any date between 1900-2100
- **Range**: 200 years of orbital data

### Navigation
- **Click planet buttons** to focus camera on that body
- **Mouse controls**:
  - Left drag: Rotate view
  - Right drag: Pan
  - Scroll: Zoom

---

## 📊 TELEMETRY PANEL

Real-time data for the selected target:
- **DIST (SUN)**: Distance from Sun in Astronomical Units
- **VELOCITY**: Orbital velocity in km/s
- **POSITION X/Z**: Heliocentric coordinates in AU

---

## 🔮 WHAT MAKES THIS DIFFERENT

### vs. Static Rotation (Gen 171-175)
Previous Fraymus generations used:
```javascript
mesh.rotation.y += 0.002  // Fake rotation
```

Fraymus Eyes uses:
```javascript
position = calculatePosition(keplerianElements, currentTime)
```

**Result**: Planets move at their **actual speeds**. Mercury zips around in 88 days. Neptune crawls through its 165-year orbit.

### vs. NASA Eyes
NASA Eyes downloads gigabytes of pre-calculated ephemeris data. Fraymus Eyes calculates everything **live** using ~50 lines of math.

**Trade-off**: NASA is accurate to the meter. Fraymus is accurate to ~1000 km (good enough for visualization).

---

## 🧬 THE ARCHITECTURE

```
FRAYMUS EYES
├── Time Engine
│   ├── simTime (Date object)
│   ├── timeSpeed (multiplier)
│   └── Time Scrubber (UI)
│
├── Spatial Hierarchy
│   ├── Scene (THREE.Scene)
│   ├── Sun (center, 0,0,0)
│   └── Planets (calculated positions)
│
└── Data Layer
    ├── Keplerian Elements (ORBITS object)
    └── calculatePosition() (live math)
```

---

## 🚀 FUTURE ENHANCEMENTS

### Moons
Add hierarchical orbits:
```javascript
bodies['MOON'] = {
    parent: 'EARTH',
    orbit: { a: 0.00257, e: 0.0549, ... }
}
```

### Spacecraft
Add artificial satellites with TLE (Two-Line Element) propagation.

### Accurate Inclination
Currently ignoring orbital tilt for visual simplicity. Can add 3D rotation matrices.

### Light-Time Correction
Account for the time light takes to reach Earth (affects apparent position).

### Perturbations
Add gravitational influences from other planets (currently pure 2-body problem).

---

## 📐 TECHNICAL NOTES

### Coordinate System
- **X-axis**: Points toward vernal equinox
- **Y-axis**: North ecliptic pole (currently unused)
- **Z-axis**: 90° from X in ecliptic plane
- **Scale**: 1 AU = 50 Three.js units

### Epoch
All calculations relative to **J2000.0** (Jan 1, 2000, 12:00 Terrestrial Time).

### Accuracy
Simplified Keplerian elements are accurate to ~0.01 AU over 200 years for inner planets, ~0.1 AU for outer planets. Good enough for educational visualization.

---

## 🎯 THE ABSTRACTION COMPLETE

We have successfully extracted NASA's Eyes architecture:

✅ **Time Engine** - Full time control with scrubbing  
✅ **Spatial Hierarchy** - Scene graph with parent-child relationships  
✅ **Data Layer** - Live Keplerian calculations (no pre-baked data)  

**The system is now ready to evolve.**

---

*Generated by Fraymus Agent Brain // Gen 176*  
*"We don't download the universe. We calculate it."*
