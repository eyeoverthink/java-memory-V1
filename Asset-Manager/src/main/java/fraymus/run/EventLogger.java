package fraymus.run;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Structured event logging
 * Writes both human-readable console and machine-readable JSONL
 */
public class EventLogger implements AutoCloseable {
    private final RunConfig config;
    private final Path runDir;
    private final ObjectMapper mapper;
    private final BufferedWriter eventsWriter;
    private final BufferedWriter metricsWriter;
    private final List<Map<String, Object>> allEvents;
    private final Map<String, Object> summary;
    
    public EventLogger(RunConfig config, String engineName) throws IOException {
        this.config = config;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.allEvents = new ArrayList<>();
        this.summary = new LinkedHashMap<>();
        
        // Create output directory
        this.runDir = config.outDir.resolve(engineName).resolve(String.valueOf(config.seed));
        Files.createDirectories(runDir);
        
        // Open writers
        if (config.jsonl) {
            this.eventsWriter = Files.newBufferedWriter(runDir.resolve("events.jsonl"));
            this.metricsWriter = Files.newBufferedWriter(runDir.resolve("metrics.csv"));
            metricsWriter.write("step,name,value\n");
        } else {
            this.eventsWriter = null;
            this.metricsWriter = null;
        }
    }
    
    public void header(Map<String, Object> meta) throws IOException {
        summary.put("config", configToMap());
        summary.put("meta", meta);
        summary.put("start_time", System.currentTimeMillis());
        
        if (config.prettyConsole) {
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║  RUN CONFIGURATION                                            ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Seed: " + config.seed);
            System.out.println("Steps: " + config.steps);
            System.out.println("Population: " + config.populationSize);
            System.out.println("Output: " + runDir);
            System.out.println();
            meta.forEach((k, v) -> System.out.println(k + ": " + v));
            System.out.println();
        }
    }
    
    public void event(String type, Map<String, Object> fields) throws IOException {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("type", type);
        event.put("timestamp", System.currentTimeMillis());
        event.putAll(fields);
        
        allEvents.add(event);
        
        if (config.jsonl && eventsWriter != null) {
            eventsWriter.write(mapper.writeValueAsString(event));
            eventsWriter.write("\n");
            eventsWriter.flush();
        }
    }
    
    public void fusionEvent(FusionEvent fusion) throws IOException {
        event("fusion", fusion.toMap());
        
        if (config.prettyConsole) {
            System.out.println("   " + fusion.toPrettyString());
        }
    }
    
    public void metric(String name, double value, int step) throws IOException {
        if (config.jsonl && metricsWriter != null) {
            metricsWriter.write(String.format("%d,%s,%.6f\n", step, name, value));
            metricsWriter.flush();
        }
    }
    
    public void footer(Map<String, Object> results) throws IOException {
        summary.put("end_time", System.currentTimeMillis());
        summary.put("results", results);
        summary.put("event_count", allEvents.size());
        
        // Write summary JSON
        Path summaryPath = runDir.resolve("run_summary.json");
        mapper.writeValue(summaryPath.toFile(), summary);
        
        if (config.prettyConsole) {
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║  RUN COMPLETE                                                 ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Summary written to: " + summaryPath);
            System.out.println("Events: " + allEvents.size());
            System.out.println();
        }
    }
    
    private Map<String, Object> configToMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("seed", config.seed);
        map.put("steps", config.steps);
        map.put("population_size", config.populationSize);
        map.put("gravity_constant", config.gravityConstant);
        map.put("fusion_distance", config.fusionDistance);
        map.put("energy_threshold", config.energyThreshold);
        return map;
    }
    
    @Override
    public void close() throws IOException {
        if (eventsWriter != null) eventsWriter.close();
        if (metricsWriter != null) metricsWriter.close();
    }
}
