package fraymus;

import fraymus.genomics.DigitalGenomeArchive;
import java.nio.file.*;
import java.util.Map;

/** Real archive relocation, hash preservation, and no-fallback regression test. */
public final class TestPortableGenomeArchive {
    public static void main(String[] args) throws Exception {
        Path base=Files.createTempDirectory("fraynix-portable-archive-");
        Path oldRoot=base.resolve("original"), newRoot=base.resolve("relocated folder");
        Path archive=oldRoot.resolve(".specialists/genomes");
        Map<String,Object> receipt=DigitalGenomeArchive.save(archive,"robot-brains",Map.of("learned",80));
        try(var walk=Files.walk(oldRoot)) {
            for(Path p:walk.toList()) {
                Path dest=newRoot.resolve(oldRoot.relativize(p));
                if(Files.isDirectory(p))Files.createDirectories(dest);else Files.copy(p,dest);
            }
        }
        System.setProperty("fraynix.originalWorkspace",oldRoot.toString());
        System.setProperty("fraynix.portableWorkspace",newRoot.toString());
        try {
            Path originalManifest=Path.of((String)receipt.get("manifest"));
            if(!DigitalGenomeArchive.readPayload(originalManifest).get("learned").toString().equals("80"))throw new AssertionError("Lost state");
            Path copied=DigitalGenomeArchive.resolveStoredPath(originalManifest);
            if(!DigitalGenomeArchive.identityPath(copied).equals(originalManifest))throw new AssertionError("Command identity changed after relocation");
            if(!java.util.Arrays.equals(Files.readAllBytes(originalManifest),Files.readAllBytes(copied)))throw new AssertionError("Manifest changed");
            var manifest=DigitalGenomeArchive.readMap(copied);
            Path payload=DigitalGenomeArchive.resolveStoredPath(Path.of((String)manifest.get("payload")));
            byte[] valid=Files.readAllBytes(payload);Files.writeString(payload,"corrupted portable copy");
            boolean rejected=false;
            try {DigitalGenomeArchive.readPayload(originalManifest);}catch(java.io.IOException expected){rejected=true;}
            if(!rejected)throw new AssertionError("Fell back to original archive or ignored corruption");
            Files.write(payload,valid);
            var next=DigitalGenomeArchive.save(newRoot.resolve(".specialists/genomes"),"robot-brains",Map.of("learned",81));
            if(!DigitalGenomeArchive.readPayload(Path.of((String)next.get("manifest"))).get("learned").toString().equals("81"))throw new AssertionError("Cannot continue learning after relocation");
            Path unrelated=base.resolve("unrelated/file");
            if(!DigitalGenomeArchive.resolveStoredPath(unrelated).equals(unrelated))throw new AssertionError("Remapped an unrelated path");
            System.out.println("PASS: relocated restore, original bytes preserved, corruption rejected, continued learning, unrelated paths preserved");
        } finally {
            System.clearProperty("fraynix.originalWorkspace");System.clearProperty("fraynix.portableWorkspace");
        }
    }
}
