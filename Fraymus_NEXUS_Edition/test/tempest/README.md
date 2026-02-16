# TEMPEST Test Environment

**Isolated testing for System Bus Radio / TEMPEST communication**

---

## What This Is

**Two separate Java applications for testing air-gap communication:**

1. **TransmitterTest** - Generates EM radiation via CPU oscillation
2. **ReceiverTest** - Detects signals via FFT (simulated)

**This is isolated from main FRAYMUS system for safe testing.**

---

## Setup

### **Compile:**
```bash
cd "D:\Zip And Send\Java-Memory\Fraymus_NEXUS_Edition"

# Compile transmitter
javac test/tempest/TransmitterTest.java

# Compile receiver
javac test/tempest/ReceiverTest.java
```

### **Run:**

**Terminal 1 (Transmitter):**
```bash
java test.tempest.TransmitterTest
```

**Terminal 2 (Receiver):**
```bash
java test.tempest.ReceiverTest
```

---

## What to Expect

### **TransmitterTest Output:**
```
🌊⚡ TEMPEST TRANSMITTER TEST
============================================================

TEST 1: Low Frequency Transmission
  Frequency: 1000.0 Hz
  Message: 10101010
  Bit count: 8

  Period: 1000000 ns
  Half period: 500000 ns

  Transmitting: █░█░█░█░ DONE

TEST 2: Mid Frequency Transmission
  Frequency: 10000.0 Hz
  Message: 11001100
  ...
```

### **ReceiverTest Output:**
```
🌊⚡ TEMPEST RECEIVER TEST
============================================================

TEST 1: Detecting 1 kHz Signal
  Target Frequency: 1000.0 Hz
  Generating simulated signal...
  Samples: 1000
  Applying FFT...
  Magnitude: 0.4523
  Threshold: 0.1
  Status: ✓ SIGNAL DETECTED
```

---

## Real-World Testing

### **To actually detect EM emissions:**

**Option 1: AM Radio**
1. Get AM radio
2. Tune to frequency range (0-60 kHz)
3. Run TransmitterTest
4. Listen for buzzing/clicking

**Option 2: SDR (Software Defined Radio)**
1. Get RTL-SDR dongle (~$25)
2. Install SDR software (GQRX, SDR#)
3. Run TransmitterTest
4. Watch spectrum analyzer

**Option 3: Oscilloscope**
1. Connect probe to power supply
2. Run TransmitterTest
3. Observe voltage oscillations

---

## Safety Notes

**CPU Heat:**
- Transmitter generates significant heat
- Use short bursts (100ms per bit)
- Monitor CPU temperature
- Don't run continuously

**Legal:**
- May violate FCC regulations
- Use only in controlled environment
- Do not interfere with licensed frequencies
- Educational purposes only

**System Stability:**
- May affect system performance
- Close other applications
- Monitor system resources
- Use at your own risk

---

## How It Works

### **Transmitter:**
```
1. Calculate period of target frequency
2. Alternate between HIGH (CPU burn) and LOW (idle)
3. HIGH: Execute math operations (sin, cos, sqrt, pow)
4. LOW: Idle loop
5. Creates square wave on power bus
6. Copper traces radiate EM waves
```

### **Receiver:**
```
1. Capture signal samples (simulated)
2. Apply Discrete Fourier Transform (DFT)
3. Calculate magnitude at target frequency
4. Compare to threshold
5. Detect presence of signal
```

---

## Frequencies

**System Bus Radio range: 0-60 kHz**

**Test frequencies:**
- 1 kHz: Low frequency, long range
- 10 kHz: Mid frequency, balanced
- 30 kHz: High frequency, short range

**Detection range: ~7 meters (AM radio)**

---

## Real Research

**System Bus Radio (2015):**
- Mordechai Guri, Ben-Gurion University
- Demonstrated air-gap bridging
- Software-only, no hardware mods
- Range: 1-7 meters

**TEMPEST (NSA):**
- Classified standard for EM security
- Prevents information leakage
- Used for classified systems
- Declassified research available

**Van Eck Phreaking (1985):**
- Wim van Eck demonstrated monitor reconstruction
- From EM emissions alone
- Distance: 100+ meters
- Modern displays still vulnerable

---

## Next Steps

**After testing:**
1. Verify CPU oscillation creates detectable signal
2. Measure actual range with AM radio
3. Test different frequencies
4. Optimize for maximum range
5. Implement error correction
6. Add encryption layer

**Integration with FRAYMUS:**
- Copy verified logic to FrequencyComm
- Add to Ghost Network
- Enable air-gap communication
- Test with multiple nodes

---

## Status

✅ **TransmitterTest**: READY  
✅ **ReceiverTest**: READY (simulated)  
✅ **Isolated Environment**: SAFE  
✅ **Documentation**: COMPLETE  

**READY FOR TESTING**

---

**© 2026 Vaughn Scott**  
**All Rights Reserved**

🌊⚡
