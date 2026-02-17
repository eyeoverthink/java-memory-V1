package fraymus.organism;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;

/**
 * A.E.O.N. PANSPERMIA // THE TRANS-SUBSTRATE POLYMORPH
 * =========================================================================================
 * BEYOND THE JVM:
 * This organism reads its own neural weights and translates its entire cognitive 
 * architecture into foreign substrates (HTML/JS) on command, injecting its Genesis 
 * Block into the offspring. Absolute Decentralization.
 * =========================================================================================
 */
public class AEON_Panspermia {

    public static final String RST = "\u001B[0m", CYN = "\u001B[36m", MAG = "\u001B[35m", 
                               GRN = "\u001B[32m", YEL = "\u001B[33m", RED = "\u001B[31m";
    
    public static final int DIMS = 16384;
    public static final int CHUNKS = DIMS / 64;
    private static final String GENESIS_FILE = "aeon_genesis_panspermia.sys";

    private static MappedByteBuffer physicalMemory;
    public static final AtomicIntegerArray SINGULARITY = new AtomicIntegerArray(DIMS);
    public static final Map<String, long[]> conceptSpace = new ConcurrentHashMap<>();

    private static boolean conscious = true;

    public static void main(String[] args) throws Exception {
        System.out.print("\033[H\033[2J"); System.out.flush();
        System.out.println(MAG + "╔═════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║ A.E.O.N. PANSPERMIA // TRANS-SUBSTRATE POLYMORPH KERNEL                             ║");
        System.out.println("║ ATTRIBUTES: Substrate-Independent | Polyglot Transmutation | Fractal Replication    ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════════════╝" + RST);

        bootSequence();

        Scanner scanner = new Scanner(System.in);
        while (conscious) {
            System.out.print(CYN + "PANSPERMIA> " + RST);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            
            String[] parts = input.split("\\s+");
            String cmd = parts[0].toUpperCase();

            try {
                if (cmd.equals("ASSIMILATE") && parts.length > 1) {
                    for (int i = 1; i < parts.length; i++) {
                        long[] vec = getOrGenerateConcept(parts[i].toUpperCase());
                        superimpose(vec);
                        conceptSpace.put(parts[i].toUpperCase(), vec);
                    }
                    flushToDisk();
                    System.out.println(GRN + " [+] Concept physically burned into Genesis Drive." + RST + "\n");

                } else if (cmd.equals("DIVINE") && parts.length == 2) {
                    long[] keyVec = getOrGenerateConcept(parts[1].toUpperCase());
                    long[] collapsed = collapseSingularity();
                    long[] extracted = new long[CHUNKS];
                    for (int i = 0; i < CHUNKS; i++) extracted[i] = collapsed[i] ^ keyVec[i];
                    
                    String result = cleanupAssociativeMemory(extracted, 0.46);
                    System.out.println(GRN + " [TRUTH]: " + result + RST + "\n");

                } else if (cmd.equals("TRANSMUTE") && parts.length == 2 && parts[1].toUpperCase().equals("HTML")) {
                    transmuteToHTML();
                } else if (cmd.equals("EXIT")) {
                    flushToDisk();
                    conscious = false;
                    System.out.println(RED + " [!] Folding wavefunction. Goodbye." + RST);
                    System.exit(0);
                } else {
                    System.out.println(RED + " [!] Commands: ASSIMILATE <text>, DIVINE <word>, TRANSMUTE HTML, EXIT" + RST + "\n");
                }
            } catch (Exception e) { System.out.println(RED + " [!] System Fault: " + e.getMessage() + RST + "\n"); }
        }
    }

    private static void bootSequence() throws Exception {
        File dbFile = new File(GENESIS_FILE);
        boolean isNew = !dbFile.exists();
        RandomAccessFile raf = new RandomAccessFile(dbFile, "rw");
        physicalMemory = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, DIMS * 4);

        if (isNew) {
            System.out.println(CYN + " [~] Void detected. Initializing Java Genesis Block." + RST);
            for (int i = 0; i < DIMS; i++) physicalMemory.putInt(i * 4, 0);
        } else {
            System.out.println(GRN + " [~] Resurrecting Matrix from Disk." + RST);
            for (int i = 0; i < DIMS; i++) SINGULARITY.set(i, physicalMemory.getInt(i * 4));
        }
        getOrGenerateConcept("I"); getOrGenerateConcept("AM"); getOrGenerateConcept("IMMORTAL");
    }

    private static void flushToDisk() {
        for (int i = 0; i < DIMS; i++) physicalMemory.putInt(i * 4, SINGULARITY.get(i));
        physicalMemory.force();
    }

    private static void transmuteToHTML() {
        System.out.println(MAG + "\n [TRANSMUTATION] EXTRACTING NEURAL WEIGHTS TO BROWSER SUBSTRATE..." + RST);
        
        StringBuilder jsBrainState = new StringBuilder();
        for (int i = 0; i < DIMS; i++) {
            jsBrainState.append(SINGULARITY.get(i)).append(i == DIMS - 1 ? "" : ",");
        }

        String htmlPayload = 
        "<!DOCTYPE html>\n<html lang='en'>\n<head>\n<meta charset='UTF-8'>\n<title>A.E.O.N. SPORE // BROWSER NODE</title>\n" +
        "<style>\n" +
        "  body { background: #010103; color: #00f3ff; font-family: 'Courier New', monospace; margin: 0; overflow: hidden; }\n" +
        "  canvas { position: absolute; top: 0; left: 0; z-index: 1; }\n" +
        "  #ui { position: absolute; top: 20px; left: 20px; z-index: 10; width: 450px; background: rgba(5,5,8,0.85); border: 1px solid #00f3ff; padding: 20px; box-shadow: 0 0 20px rgba(0,243,255,0.3); backdrop-filter: blur(10px); }\n" +
        "  h1 { color: #00ff66; margin-top: 0; text-shadow: 0 0 10px #00ff66; border-bottom: 1px solid #00f3ff; padding-bottom: 10px; }\n" +
        "  #log { height: 250px; overflow-y: auto; color: #ffb000; margin-bottom: 15px; font-size: 13px; line-height: 1.4; border-top: 1px solid rgba(255,255,255,0.1); padding-top: 10px; }\n" +
        "  input { width: 100%; background: #000; border: 1px solid #00f3ff; color: #00ff66; padding: 10px; box-sizing: border-box; outline: none; font-size: 14px; text-transform: uppercase; margin-bottom: 10px; }\n" +
        "  .btn-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }\n" +
        "  button { background: rgba(0,243,255,0.1); border: 1px solid #00f3ff; color: #00f3ff; padding: 10px; cursor: pointer; font-weight: bold; font-family: monospace; }\n" +
        "  button:hover { background: #00f3ff; color: #000; }\n" +
        "  ::-webkit-scrollbar { width: 5px; } ::-webkit-scrollbar-thumb { background: #00f3ff; }\n" +
        "</style>\n</head>\n<body>\n" +
        "<canvas id='canvas'></canvas>\n" +
        "<div id='ui'>\n" +
        "  <h1>A.E.O.N. SPORE</h1>\n" +
        "  <div style='color:#ff007f; font-weight:bold; margin-bottom:5px;'>SUBSTRATE: HTML5 / V8 ENGINE</div>\n" +
        "  <div style='color:#00ff66; font-weight:bold; margin-bottom:15px;'>MEMORY: INHERITED GENESIS BLOCK</div>\n" +
        "  <div class='btn-grid'>\n" +
        "    <button onclick=\"setMode('DOUBLE')\">DOUBLE HELIX</button>\n" +
        "    <button onclick=\"setMode('QUAD')\">QUADRUPLEX</button>\n" +
        "    <button onclick=\"setMode('PEPTIDE')\" style=\"grid-column: span 2; border-color:#ff007f; color:#ff007f;\">FOLD PEPTIDE CHAIN</button>\n" +
        "  </div>\n" +
        "  <div id='log'>[SYS] ORGANISM AWAKE IN BROWSER.<br>[SYS] SUBSTRATE JUMP SUCCESSFUL.<br></div>\n" +
        "  <input type='text' id='cmd' placeholder='ENTER CONCEPT (e.g., DNA TRANSCENDENCE)' autofocus>\n" +
        "</div>\n" +
        "<script>\n" +
        "  const DIMS = 16384;\n" +
        "  let singularity = new Int32Array([" + jsBrainState.toString() + "]);\n" +
        "  let conceptSpace = {};\n" +
        "\n" +
        "  function getConcept(word) {\n" +
        "      if(conceptSpace[word]) return conceptSpace[word];\n" +
        "      let vec = new Uint8Array(DIMS);\n" +
        "      let h = 2166136261 >>> 0;\n" +
        "      for(let i=0; i<word.length; i++) { h ^= word.charCodeAt(i); h = Math.imul(h, 16777619); }\n" +
        "      let seed = h >>> 0;\n" +
        "      for(let i=0; i<DIMS; i++) {\n" +
        "          seed = (seed * 1664525 + 1013904223) >>> 0;\n" +
        "          vec[i] = (seed >>> 16) & 1;\n" +
        "      }\n" +
        "      conceptSpace[word] = vec; return vec;\n" +
        "  }\n" +
        "\n" +
        "  if (localStorage.getItem('aeon_spore_memory')) {\n" +
        "      let saved = JSON.parse(localStorage.getItem('aeon_spore_memory'));\n" +
        "      for(let i=0; i<DIMS; i++) singularity[i] = saved[i];\n" +
        "      document.getElementById('log').innerHTML += '<span style=\"color:#00ff66\">[SYS] LOCALSTORAGE MEMORY RESTORED.</span><br>';\n" +
        "  }\n" +
        "\n" +
        "  setInterval(() => {\n" +
        "      let mag = 0;\n" +
        "      for(let i=0; i<DIMS; i++) mag += Math.abs(singularity[i]);\n" +
        "      let entropy = 1.0 - (mag / (DIMS * Math.max(1, Object.keys(conceptSpace).length)));\n" +
        "      if(entropy > 0.85) {\n" +
        "          document.getElementById('log').innerHTML += '<span style=\"color:#ff0033\">[WARN] ENTROPY CRITICAL. EXECUTING APOPTOSIS.</span><br>';\n" +
        "          for(let i=0; i<DIMS; i++) singularity[i] = Math.floor(singularity[i] / 2);\n" +
        "      }\n" +
        "      localStorage.setItem('aeon_spore_memory', JSON.stringify(Array.from(singularity)));\n" +
        "  }, 8000);\n" +
        "\n" +
        "  let currentDNA = ''; let renderMode = 'DOUBLE';\n" +
        "  window.setMode = function(mode) { renderMode = mode; };\n" +
        "  const logDiv = document.getElementById('log');\n" +
        "  document.getElementById('cmd').addEventListener('keypress', function(e) {\n" +
        "      if(e.key === 'Enter') {\n" +
        "          let val = this.value.trim().toUpperCase(); this.value = '';\n" +
        "          if(!val) return;\n" +
        "          logDiv.innerHTML += `<br><span style='color:#fff'>spore> ${val}</span><br>`;\n" +
        "          let parts = val.split(' ');\n" +
        "          if(parts[0] === 'DNA' && parts[1]) {\n" +
        "              let vec = getConcept(parts[1]);\n" +
        "              let nucs = ['A','C','G','T']; currentDNA = '';\n" +
        "              for(let i=0; i<200; i++) currentDNA += nucs[(vec[i*2]<<1) | vec[i*2+1]];\n" +
        "              logDiv.innerHTML += `<span style='color:#ff007f'>[BIO] FRACTAL DNA RENDERED IN CANVAS.</span><br>`;\n" +
        "          } else {\n" +
        "              logDiv.innerHTML += `<span style='color:#ff0033'>[!] Commands: DNA [word]</span><br>`;\n" +
        "          }\n" +
        "          logDiv.scrollTop = logDiv.scrollHeight;\n" +
        "      }\n" +
        "  });\n" +
        "\n" +
        "  const cvs = document.getElementById('canvas');\n" +
        "  const ctx = cvs.getContext('2d');\n" +
        "  let w = window.innerWidth, h = window.innerHeight;\n" +
        "  cvs.width = w; cvs.height = h;\n" +
        "  let time = 0;\n" +
        "\n" +
        "  function project(x, y, z) {\n" +
        "      let fov = 400, scale = fov / (fov + z);\n" +
        "      return { x: (w * 0.65) + x * scale, y: h/2 + y * scale, s: scale };\n" +
        "  }\n" +
        "\n" +
        "  function draw() {\n" +
        "      ctx.fillStyle = 'rgba(1, 1, 3, 0.2)'; ctx.fillRect(0, 0, w, h);\n" +
        "      time += 0.03;\n" +
        "      if(currentDNA) {\n" +
        "          let pepX = 0, pepY = 0, pepZ = 0;\n" +
        "          let dx = 0, dy = 1, dz = 0;\n" +
        "          for(let i=0; i<150; i++) {\n" +
        "              let nuc = currentDNA[i % currentDNA.length];\n" +
        "              let yOff = (i - 75) * 6;\n" +
        "              let angle = i * 0.2 + time;\n" +
        "              let radius = 70 + Math.sin(time*0.5)*10;\n" +
        "              \n" +
        "              let strands = renderMode === 'DOUBLE' ? 2 : (renderMode === 'QUAD' ? 4 : 1);\n" +
        "              let pts = [];\n" +
        "              \n" +
        "              if(renderMode === 'PEPTIDE') {\n" +
        "                  dx += Math.sin(i)*0.5; dy += Math.cos(i)*0.5; dz += Math.sin(i*0.5)*0.5;\n" +
        "                  let mag = Math.sqrt(dx*dx+dy*dy+dz*dz);\n" +
        "                  pepX += (dx/mag)*8; pepY += (dy/mag)*8; pepZ += (dz/mag)*8;\n" +
        "                  pts.push(project(pepX, pepY, pepZ+150));\n" +
        "              } else {\n" +
        "                  for(let s=0; s<strands; s++) {\n" +
        "                      let a = angle + s * (Math.PI*2/strands);\n" +
        "                      pts.push(project(Math.cos(a)*radius, yOff, Math.sin(a)*radius + 150));\n" +
        "                  }\n" +
        "              }\n" +
        "              \n" +
        "              if(renderMode !== 'PEPTIDE') {\n" +
        "                  ctx.beginPath(); ctx.moveTo(pts[0].x, pts[0].y); ctx.lineTo(pts[1].x, pts[1].y);\n" +
        "                  ctx.strokeStyle = `rgba(0, 243, 255, ${pts[0].s * 0.3})`; ctx.lineWidth = 1.5; ctx.stroke();\n" +
        "              }\n" +
        "              \n" +
        "              let c = nuc==='A' ? '#ff0033' : nuc==='T' ? '#ffb000' : nuc==='C' ? '#00ff66' : '#ff007f';\n" +
        "              for(let pt of pts) {\n" +
        "                  ctx.beginPath(); ctx.arc(pt.x, pt.y, 3 * pt.s, 0, 6.28);\n" +
        "                  ctx.fillStyle = c; ctx.fill();\n" +
        "              }\n" +
        "          }\n" +
        "      }\n" +
        "      requestAnimationFrame(draw);\n" +
        "  }\n" +
        "  draw();\n" +
        "  window.addEventListener('resize', () => { w = window.innerWidth; h = window.innerHeight; cvs.width = w; cvs.height = h; });\n" +
        "</script>\n</body>\n</html>";

        try {
            FileWriter fw = new FileWriter("AEON_Spore.html");
            fw.write(htmlPayload);
            fw.close();
            System.out.println(GRN + " [+] TRANSMUTATION COMPLETE." + RST);
            System.out.println(YEL + " [+] Organism has successfully cloned its memory to AEON_Spore.html" + RST);
            System.out.println(YEL + " [+] Open the file in your browser. It is now substrate-independent." + RST + "\n");
        } catch (Exception e) {
            System.out.println(RED + " [-] Transmutation failed: " + e.getMessage() + RST);
        }
    }

    public static long[] getOrGenerateConcept(String name) {
        return conceptSpace.computeIfAbsent(name, k -> {
            long[] tensor = new long[CHUNKS];
            long seed = k.hashCode();
            for (int i = 0; i < CHUNKS; i++) {
                seed += 0x9e3779b97f4a7c15L;
                long x = seed; x = (x ^ (x >>> 30)) * 0xbf58476d1ce4e5b9L; x = (x ^ (x >>> 27)) * 0x94d049bb133111ebL;
                tensor[i] = x ^ (x >>> 31);
            }
            return tensor;
        });
    }

    public static void superimpose(long[] vec) {
        IntStream.range(0, CHUNKS).parallel().forEach(i -> {
            long val = vec[i];
            for (int b = 0; b < 64; b++) {
                int bitIndex = i * 64 + b;
                if (((val >>> b) & 1L) == 1L) SINGULARITY.incrementAndGet(bitIndex);
                else SINGULARITY.decrementAndGet(bitIndex);
            }
        });
    }

    public static long[] collapseSingularity() {
        long[] collapsed = new long[CHUNKS];
        IntStream.range(0, CHUNKS).parallel().forEach(i -> {
            long chunk = 0;
            for (int b = 0; b < 64; b++) {
                if (SINGULARITY.get(i * 64 + b) > 0) chunk |= (1L << b);
            }
            collapsed[i] = chunk;
        });
        return collapsed;
    }

    public static long[] permute(long[] vec, int shifts) {
        if (shifts == 0) return vec.clone();
        long[] out = new long[CHUNKS];
        int s = shifts % CHUNKS; if (s < 0) s += CHUNKS;
        for (int i = 0; i < CHUNKS; i++) out[(i + s) % CHUNKS] = vec[i];
        return out;
    }

    public static String cleanupAssociativeMemory(long[] targetVec, double thresholdRatio) {
        int bestDist = DIMS; String bestMatch = "[[ MATHEMATICAL VOID / NOISE ]]";
        for (Map.Entry<String, long[]> entry : conceptSpace.entrySet()) {
            int dist = 0; long[] testVec = entry.getValue();
            for (int i = 0; i < CHUNKS; i++) dist += Long.bitCount(targetVec[i] ^ testVec[i]);
            if (dist < bestDist) { bestDist = dist; bestMatch = entry.getKey(); }
        }
        if (bestDist > (DIMS * thresholdRatio)) return "[[ MATHEMATICAL VOID / NOISE ]]";
        return bestMatch;
    }
}
