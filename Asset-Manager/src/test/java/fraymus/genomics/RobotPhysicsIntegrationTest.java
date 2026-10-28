package fraymus.genomics;

import fraymus.FraynixBoot;
import fraymus.core.FraymusJSON;
import fraymus.worldmodel.WorldModel;
import fraymus.embodied.EmbodiedAI;
import fraymus.knowledge.AkashicRecord;
import fraymus.neural.AeonKronos;
import fraymus.brain.unified.UnifiedBrainOrchestrator;
import java.nio.file.*;
import java.util.*;

/** Separate JVMs, actual editor worker, JBox2D, existing brains and three memory destinations. */
public final class RobotPhysicsIntegrationTest {
    static void require(boolean condition,String message){if(!condition)throw new AssertionError(message);}
    static String state(java.io.Serializable brain)throws Exception {
        var bytes=new java.io.ByteArrayOutputStream();try(var stream=new java.io.ObjectOutputStream(bytes)){stream.writeObject(brain);}
        return DigitalGenomeArchive.sha(bytes.toByteArray());
    }
    static void field(String name,Object value)throws Exception{var f=FraynixBoot.class.getDeclaredField(name);f.setAccessible(true);f.set(null,value);}
    @SuppressWarnings("unchecked") static Map<String,Object> call(Map<String,Object> request)throws Exception {
        Path file=Path.of("request-"+DigitalGenomeArchive.sha(DigitalGenomeArchive.jsonBytes(request))+".json");DigitalGenomeArchive.atomicWrite(file,DigitalGenomeArchive.jsonBytes(request));
        var method=FraynixBoot.class.getDeclaredMethod("executeSpecialistCommand",String.class);method.setAccessible(true);
        var result=(CarbonGenomicsBridge.Result)method.invoke(null,"robot "+file.toAbsolutePath());require(result.success(),result.output());return (Map<String,Object>)FraymusJSON.parseStrict(result.output());
    }
    @SuppressWarnings("unchecked") public static void main(String[] args)throws Exception {
        System.setProperty("fraynix.kronos.bootstrap.enabled","false");
        var memory=new AkashicRecord();var kronos=AeonKronos.getInstance();var world=new WorldModel(4,new int[]{100,50,25,10});var body=new EmbodiedAI(50,20);body.initialize();
        var orchestrator=new UnifiedBrainOrchestrator();orchestrator.wireHolographicMemory();orchestrator.wirePhase5Components(null,world,body,null,null,null);
        field("akashic",memory);field("kronos",kronos);field("unifiedOrchestrator",orchestrator);
        Map<String,Object> base=new LinkedHashMap<>();base.put("backend","fraymus-scene");base.put("operation","observe");
        try {
            if(args.length>0) {
                var saved=DigitalGenomeArchive.readMap(Path.of("robot-proof.json"));
                require(((Number)saved.get("interactions")).longValue()==body.getStatistics().totalInteractions,"Boot composition did not restore learned body state before commands");
                require(Arrays.stream(world.predictNextState(new double[100])).boxed().toList().equals(saved.get("prediction")),"Boot composition did not restore world parameters before commands");
            }
            var observed=call(base);
            if(args.length>0) {
                var proof=DigitalGenomeArchive.readMap(Path.of("robot-proof.json"));
                require(((Number)proof.get("interactions")).longValue()==body.getStatistics().totalInteractions,"Actual body learning did not survive JVM restart");
                require(Arrays.stream(world.predictNextState(new double[100])).boxed().toList().equals(proof.get("prediction")),"Actual saved world parameters/state were not restored");
                require(((Map<String,Object>)observed.get("after")).get("sceneHash").equals(proof.get("scene_hash")),"Scene state did not survive restart");
                require(kronos.getBindCount()==1 || kronos.getBindCount()==0,"Existing specialist evidence was duplicated");
                Files.writeString(Path.of("robot-restart-proof.json"),FraymusJSON.stringify(Map.of("ok",true,"actual_brains_restored",true,"scene_restored",true,"interactions",body.getStatistics().totalInteractions)));
                System.out.println("PASS: fresh JVM restores actual scene, body learning and world parameters");return;
            }
            var observation=(Map<String,Object>)observed.get("after");String hash=(String)observation.get("sceneHash");
            List<Map<String,Object>> operations=List.of(Map.of("op","create","key","robot","name","Fraynix robot","x",0,"y",2,"width",.25,"height",.25),
                Map.of("op","add_component","entityId","$robot","component","physics2d.components.Rigidbody2D"),
                Map.of("op","add_component","entityId","$robot","component","physics2d.components.Box2DCollider"),
                Map.of("op","set","entityId","$robot","component","physics2d.components.Rigidbody2D","values",Map.of("bodyType","Kinematic")));
            var create=new LinkedHashMap<>(base);create.put("operation","apply");create.put("command_id","create-native-robot");create.put("expected_scene_hash",hash);
            create.put("proposal",Map.of("kind","proposal","observationId",hash.substring(0,16),"summary","Create a physics robot using the existing factory","operations",operations));
            var created=call(create);require(Boolean.TRUE.equals(created.get("executed")),"Creation was not committed");
            var entity=(Map<String,Object>)((List<?>)((Map<?,?>)created.get("after")).get("entities")).get(0);
            var control=new LinkedHashMap<>(base);control.put("operation","control");control.put("command_id","native-control-one");control.put("entity_id",entity.get("entityId"));
            control.put("speed_scale",4);control.put("motor_units","world-units-per-second");control.put("target_position",List.of(.5,2));control.put("seconds",.25);control.put("reward",.75);
            var moved=call(control);require(Boolean.TRUE.equals(moved.get("executed")) && Boolean.TRUE.equals(moved.get("feedback_applied")),"Actual physics did not feed existing brains");
            require(Boolean.FALSE.equals(moved.get("physical_hardware_executed")),"Simulated actuation was misreported as physical");
            long interactions=body.getStatistics().totalInteractions;var repeated=call(control);
            require(Boolean.TRUE.equals(repeated.get("replayed_receipt")) && interactions==body.getStatistics().totalInteractions,"Duplicate command executed or learned twice");
            String priorWorld=state(world),priorBody=state(body);
            var rejected=new LinkedHashMap<>(control);rejected.put("command_id","invalid-target-control");rejected.put("target_position",List.of(1));
            try {call(rejected);throw new AssertionError("Invalid entity control was accepted");}catch(AssertionError expected) {
                require(expected.getMessage().contains("Target position requires two finite scene coordinates"),"Unexpected failure: "+expected.getMessage());
            }
            require(priorWorld.equals(state(world)) && priorBody.equals(state(body)),"Rejected action altered learned brain state");
            require(memory.verifyIntegrity(),"Shared-memory chain failed");
            require(orchestrator.getHolographicMemory().getStoredKeys().stream().anyMatch(k->k.startsWith("SPECIALIST:")),"Robot evidence bypassed HDC memory");
            Files.writeString(Path.of("robot-proof.json"),FraymusJSON.stringify(Map.of("ok",true,"creation",created,"control",moved,"interactions",interactions,
                "prediction",Arrays.stream(world.predictNextState(new double[100])).boxed().toList(),"scene_hash",((Map<?,?>)moved.get("after")).get("sceneHash"))));
            System.out.println("PASS: actual creation, physics actuation, shared-brain feedback, command dedupe and recoverable learning");
        } finally{kronos.shutdown();memory.saveAll();}
    }
}
