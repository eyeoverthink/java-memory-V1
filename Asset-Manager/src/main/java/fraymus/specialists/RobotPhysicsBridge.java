package fraymus.specialists;

import fraymus.embodied.EmbodiedAI;
import fraymus.worldmodel.WorldModel;
import fraymus.genomics.DigitalGenomeArchive;
import java.nio.file.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

/** The existing scene is the environment; the existing Java brains consume its observations. */
public final class RobotPhysicsBridge {
    private static final Map<WorldModel,Set<String>> restored = new WeakHashMap<>();
    private RobotPhysicsBridge() {}
    @SuppressWarnings("unchecked") public static synchronized void restore(WorldModel world, EmbodiedAI body) throws Exception {
        if(world==null || body==null || restored.containsKey(world))return;
        var applied=new LinkedHashSet<String>(); Path latest=Path.of(".specialists/genomes/latest-robot-brains.json");
        if(Files.isRegularFile(latest)) {
            var receipt=DigitalGenomeArchive.readMap(latest); var payload=DigitalGenomeArchive.readPayload(Path.of((String)receipt.get("manifest")));
            WorldModel savedWorld=BrainSnapshots.decode((String)payload.get("world_state"),WorldModel.class);
            EmbodiedAI savedBody=BrainSnapshots.decode((String)payload.get("body_state"),EmbodiedAI.class);
            if(savedWorld.getSensoryDimension()!=world.getSensoryDimension() || savedBody.getSensoryDimension()!=body.getSensoryDimension() || savedBody.getMotorDimension()!=body.getMotorDimension())throw new IOException("Saved robot brain dimensions differ from the wired brains");
            world.restoreState(savedWorld);body.restoreState(savedBody);
            for(Object id:(List<?>)payload.get("applied_feedback"))applied.add(String.valueOf(id));
        }
        restored.put(world,applied);
    }
    private static Map<String,Object> save(WorldModel world,EmbodiedAI body)throws Exception {
        return DigitalGenomeArchive.save(Path.of(".specialists/genomes"),"robot-brains",Map.of("format","fraynix.scene-brains.v1",
            "world_state",BrainSnapshots.encode(world),"body_state",BrainSnapshots.encode(body),"applied_feedback",new ArrayList<>(restored.get(world)),
            "scope","actual existing WorldModel and EmbodiedAI state; simulator experience is labelled separately from physical observation"));
    }
    @SuppressWarnings("unchecked") public static synchronized Map<String,Object> execute(Path source,Map<String,Object> config,WorldModel world,EmbodiedAI body)throws Exception {
        if(world==null || body==null)throw new IOException("Wire WorldModel and EmbodiedAI before robot commands");restore(world,body);
        String baselineWorld=BrainSnapshots.encode(world),baselineBody=BrainSnapshots.encode(body);
        Set<String> baselineFeedback=new LinkedHashSet<>(restored.get(world));boolean brainCommitted=false;
        try {
        Map<String,Object> request=DigitalGenomeArchive.readMap(source);String operation=String.valueOf(request.get("operation"));
        if(request.containsKey("reward") && (!(request.get("reward") instanceof Number reward) || !Double.isFinite(reward.doubleValue())))throw new IOException("Feedback reward must be finite");
        if(request.containsKey("seconds") && (number(request,"seconds")<=0 || number(request,"seconds")>60))throw new IOException("Scene duration must be in (0,60] seconds");
        if(operation.equals("control")) {
            if(!"world-units-per-second".equals(request.get("motor_units")) || number(request,"speed_scale")<=0 || number(request,"speed_scale")>Float.MAX_VALUE)throw new IOException("Declare a finite positive scene speed scale and world-units-per-second units");
            if(request.get("normalized_motor") instanceof List<?> motor) {
                if(motor.size()!=2)throw new IOException("Two motor channels are required");
                for(Object value:motor)if(!(value instanceof Number n)||!Double.isFinite(n.doubleValue())||Math.abs(n.doubleValue())>1)throw new IOException("Invalid normalized motor value");
            }
        }
        String id=String.valueOf(request.getOrDefault("command_id","read-"+UUID.randomUUID()));
        if(!id.matches("[A-Za-z0-9_-]{1,96}"))throw new IOException("Invalid robot command identity");
        boolean mutation=Set.of("apply","import","control","step","stop","resume").contains(operation);
        if(mutation && !request.containsKey("command_id"))throw new IOException("Mutating robot commands require command_id");
        String originalHash=DigitalGenomeArchive.sha(Files.readAllBytes(source));
        Path workspace=DigitalGenomeArchive.resolveStoredPath(Path.of(String.valueOf(request.getOrDefault("workspace",Path.of(".specialists/physics-world").toAbsolutePath().toString())))).toAbsolutePath();
        String identity=DigitalGenomeArchive.identityPath(workspace)+":"+id;
        Path receipts=Files.createDirectories(Path.of(".specialists/robot-dispatch"));String token=DigitalGenomeArchive.sha(identity.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        Path cached=receipts.resolve(token+".json"),pending=receipts.resolve(token+".pending.json");
        if(mutation && Files.isRegularFile(cached)) {
            var saved=DigitalGenomeArchive.readMap(cached);
            if(!originalHash.equals(saved.get("original_request_sha256")))throw new IOException("Robot command identity reused with different input");
            var result=new LinkedHashMap<>((Map<String,Object>)saved.get("result"));
            for(String field:List.of("output","command_checkpoint","checkpoint","report","brain_state_manifest","request"))
                if(result.get(field) instanceof String path)result.put(field,DigitalGenomeArchive.resolveStoredPath(Path.of(path)).toString());
            result.put("replayed_receipt",true);return result;
        }
        Map<String,Object> forward=new LinkedHashMap<>(request);
        if(mutation && Files.isRegularFile(pending)) {
            var saved=DigitalGenomeArchive.readMap(pending);
            if(!originalHash.equals(saved.get("original_request_sha256")))throw new IOException("Pending robot command identity conflict");
            forward=new LinkedHashMap<>((Map<String,Object>)saved.get("forward"));
        } else if(operation.equals("control") && !request.containsKey("normalized_motor")) {
            var observation=run(Map.of("operation","observe","entity_id",request.get("entity_id")),workspace,config);
            if(Boolean.TRUE.equals(observation.get("stop_latched")))throw new IOException("Robot stop is latched");
            var before=(Map<String,Object>)observation.get("after");double[] features=features(before,request,world.getSensoryDimension());
            if(request.containsKey("expected_scene_hash") && !request.get("expected_scene_hash").equals(before.get("sceneHash")))throw new IOException("Stale scene observation");
            world.processInput(features);var sensory=input(features,body);var proposal=body.process(sensory);
            double x=proposal.vector[0],y=proposal.vector[1];
            if(request.get("target_position") instanceof List<?> target) {
                if(target.size()!=2 || !(target.get(0) instanceof Number tx) || !(target.get(1) instanceof Number ty) || !Double.isFinite(tx.doubleValue()) || !Double.isFinite(ty.doubleValue()))throw new IOException("Target position requires two finite scene coordinates");
                Map<String,Object> entity=entity(before,request);var position=position(entity);
                double speed=number(request,"speed_scale");
                if(speed<=0)throw new IOException("Speed scale must be positive");
                x=2*(tx.doubleValue()-position[0])/speed+.1*x;y=2*(ty.doubleValue()-position[1])/speed+.1*y;
            }
            forward.put("normalized_motor",List.of(Math.max(-1,Math.min(1,x)),Math.max(-1,Math.min(1,y))));
            forward.putIfAbsent("expected_scene_hash",before.get("sceneHash"));
        }
        if(mutation && !Files.isRegularFile(pending))DigitalGenomeArchive.atomicWrite(pending,DigitalGenomeArchive.jsonBytes(Map.of("original_request_sha256",originalHash,"forward",forward)));
        Map<String,Object> result=new LinkedHashMap<>(run(forward,workspace,config));
        boolean learned=false;
        if(Boolean.TRUE.equals(result.get("executed")) && !operation.equals("stop") && restored.get(world).add(identity)) {
            var before=(Map<String,Object>)result.get("before");var after=(Map<String,Object>)result.get("after");
            double[] previous=features(before,request,world.getSensoryDimension()),observed=features(after,request,world.getSensoryDimension());
            double[] motor=new double[body.getMotorDimension()];
            if(forward.get("normalized_motor") instanceof List<?> values)for(int i=0;i<Math.min(values.size(),motor.length);i++)motor[i]=((Number)values.get(i)).doubleValue();
            double reward=request.get("reward") instanceof Number r?r.doubleValue():0;
            if(!Double.isFinite(reward))throw new IOException("Non-finite feedback reward");
            world.processInput(observed);body.learnTransition(input(previous,body),new EmbodiedAI.MotorCommand("scene_"+operation,motor),input(observed,body),reward);learned=true;
        }
        Map<String,Object> capsule=save(world,body);
        brainCommitted=true;
        result.put("feedback_applied",learned);result.put("feedback_domain","JBox2D simulator observations; no physical sensor claim");
        result.put("world_predictions",world.getStatistics().totalPredictions);result.put("body_interactions",body.getStatistics().totalInteractions);
        result.put("world_model_prediction",Arrays.stream(world.predictNextState(features((Map<String,Object>)result.get("after"),request,world.getSensoryDimension()))).boxed().toList());
        result.put("brain_state_manifest",capsule.get("manifest"));result.put("feature_schema","fraynix.scene-features.v1: lossy numeric summary; complete scene JSON is retained for model queries");
        result.put("request",source.toAbsolutePath().toString());result.put("request_sha256",originalHash);
        if(result.getOrDefault("command_checkpoint",result.get("checkpoint")) instanceof String checkpoint && Files.isRegularFile(Path.of(checkpoint))) {
            result.put("output",checkpoint);result.put("output_sha256",DigitalGenomeArchive.sha(Files.readAllBytes(Path.of(checkpoint))));
        }
        Path report=Path.of("reports/specialists/robot-"+UUID.randomUUID()+".json").toAbsolutePath();
        DigitalGenomeArchive.atomicWrite(report,DigitalGenomeArchive.jsonBytes(result));result.put("report",report.toString());result.put("report_sha256",DigitalGenomeArchive.sha(Files.readAllBytes(report)));
        if(mutation) {DigitalGenomeArchive.atomicWrite(cached,DigitalGenomeArchive.jsonBytes(Map.of("original_request_sha256",originalHash,"result",result)));Files.deleteIfExists(pending);}
        return result;
        } catch(Exception failure) {
            // An uncertain scene outcome is retried with its frozen command identity. Neural feedback
            // becomes committed only with the durable capsule, so failed pre-commit work can be replayed.
            if(!brainCommitted) {
                world.restoreState(BrainSnapshots.decode(baselineWorld,WorldModel.class));
                body.restoreState(BrainSnapshots.decode(baselineBody,EmbodiedAI.class));
                restored.put(world,baselineFeedback);
            }
            throw failure;
        }
    }
    private static EmbodiedAI.SensoryInput input(double[] features,EmbodiedAI body) {
        return new EmbodiedAI.SensoryInput(Arrays.copyOf(features,body.getSensoryDimension()),Arrays.copyOf(features,body.getMotorDimension()));
    }
    private static double number(Map<String,Object> map,String key)throws IOException {if(!(map.get(key) instanceof Number n)||!Double.isFinite(n.doubleValue()))throw new IOException("Expected finite "+key);return n.doubleValue();}
    @SuppressWarnings("unchecked") private static Map<String,Object> entity(Map<String,Object> observation,Map<String,Object> request)throws IOException {
        var entities=(List<Map<String,Object>>)observation.get("entities");
        if(entities.isEmpty())return Map.of();
        if(request.get("entity_id") instanceof String id) return entities.stream().filter(e->id.equals(e.get("entityId"))).findFirst().orElseThrow(()->new IOException("Observed entity missing"));
        return entities.get(0);
    }
    @SuppressWarnings("unchecked") private static double[] position(Map<String,Object> entity)throws IOException {
        if(entity.isEmpty())return new double[2];
        var components=(List<Map<String,Object>>)entity.get("components");
        for(var component:components)if("jade.Transform".equals(component.get("type"))) {
            var props=(Map<String,Object>)component.get("properties");var pos=(Map<String,Object>)props.get("position");return new double[]{number(pos,"x"),number(pos,"y")};
        }
        throw new IOException("Scene entity has no transform");
    }
    @SuppressWarnings("unchecked") private static double[] features(Map<String,Object> observation,Map<String,Object> request,int dimension)throws IOException {
        double[] features=new double[dimension];var entities=(List<Map<String,Object>>)observation.get("entities");
        var selected=entity(observation,request);double[] position=position(selected);
        double[] values=new double[]{Math.tanh(position[0]/64),Math.tanh(position[1]/64),Math.tanh(entities.size()/100.0),Math.tanh(((Number)observation.get("physicsBodies")).doubleValue()/100)};
        System.arraycopy(values,0,features,0,Math.min(values.length,dimension));
        int offset=4;var sorted=new ArrayList<>(entities);sorted.sort(Comparator.comparing(e->String.valueOf(e.get("entityId"))));
        for(var item:sorted) {if(offset+2>features.length)break;var xy=position(item);features[offset++]=Math.tanh(xy[0]/64);features[offset++]=Math.tanh(xy[1]/64);}
        return features;
    }
    private static Map<String,Object> run(Map<String,Object> request,Path workspace,Map<String,Object> config)throws Exception {
        if(!(config.get("physics_root") instanceof String root))throw new IOException("Configure physics_root with the working Fraymus scene project");
        Path classpath=Path.of(root,"build/robot-scene/classpath.txt");
        if(!Files.isRegularFile(classpath))throw new IOException("Build the scene bridge with writeRobotSceneClasspath");
        Path directory=Files.createDirectories(Path.of("reports/specialists/scene-worker"));String id=UUID.randomUUID().toString();
        Path input=directory.resolve(id+".request.json").toAbsolutePath(),output=directory.resolve(id+".response.json").toAbsolutePath(),errors=directory.resolve(id+".log").toAbsolutePath();
        DigitalGenomeArchive.atomicWrite(input,DigitalGenomeArchive.jsonBytes(request));
        var process=new ProcessBuilder(Path.of(System.getProperty("java.home"),"bin/java.exe").toString(),"-cp",Files.readString(classpath).trim(),
            "factory.RobotSceneWorker",workspace.toString(),input.toString(),output.toString()).directory(Path.of(root).toFile()).redirectError(errors.toFile()).redirectOutput(ProcessBuilder.Redirect.appendTo(errors.toFile())).start();
        try {
            if(!process.waitFor(240,TimeUnit.SECONDS))throw new IOException("Scene worker timeout; command outcome must be reconciled using the same identity");
            var result=DigitalGenomeArchive.readMap(output);
            if(process.exitValue()!=0 || !Boolean.TRUE.equals(result.get("ok")))throw new IOException("Scene worker: "+result.get("error"));return result;
        } finally {if(process.isAlive())process.destroyForcibly();}
    }
}
