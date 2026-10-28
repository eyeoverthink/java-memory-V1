package fraymus.genomics;

import fraymus.knowledge.AkashicRecord;
import java.nio.file.*;
import java.util.*;

/** A missing historical record stays explicit while available state remains runnable and recoverable. */
public final class AkashicRecordRecoveryTest {
    @SuppressWarnings("unchecked") public static void main(String[] args)throws Exception {
        var memory=new AkashicRecord();
        if(args[0].equals("seed")) {
            Path isolated=Files.createTempDirectory("record-corruption-");var archive=new fraymus.knowledge.KnowledgeRecordArchive(isolated);
            var block=new AkashicRecord.KnowledgeBlock("CHECK","independent corruption probe");archive.save(block);
            Path payload;try(var files=Files.list(isolated.resolve("payloads"))){payload=files.findFirst().orElseThrow();}
            Files.write(payload,new byte[]{0,1,2});
            try{archive.save(block);throw new AssertionError("Corrupt existing payload was acknowledged");}catch(java.io.IOException expected){}
            try{archive.load(archive.records().get(0));throw new AssertionError("Corrupt archived record was restored");}catch(java.io.IOException expected){}
            String first=memory.addBlock("FIRST","alpha");String second=memory.addBlock("SECOND","beta");
            var field=AkashicRecord.class.getDeclaredField("chainHashes");field.setAccessible(true);((List<String>)field.get(null)).add("\u03c6-0000000000000000");memory.saveAll();
            if(memory.verifyIntegrity())throw new AssertionError("Broken historical chain was accepted");
            var repaired=memory.reconcileChain();var after=(Map<?,?>)repaired.get("after");
            if(!Boolean.TRUE.equals(after.get("active_chain_valid")) || !Boolean.FALSE.equals(after.get("historical_complete")))throw new AssertionError("Reconciliation concealed historical gaps");
            Files.writeString(Path.of("identities.json"),fraymus.core.FraymusJSON.stringify(Map.of("first",first,"second",second)));
        } else {
            var ids=DigitalGenomeArchive.readMap(Path.of("identities.json"));
            if(!memory.getBlock((String)ids.get("first")).content.equals("alpha") || !memory.getBlock((String)ids.get("second")).content.equals("beta"))throw new AssertionError("Record files failed to recover a lost aggregate");
            if(!memory.verifyIntegrity() || !Boolean.FALSE.equals(memory.integrityReport().get("historical_complete")))throw new AssertionError("Restart lost active integrity or the historical-gap disclosure");
        }
        System.out.println("PASS: active-chain reconciliation, explicit historical gaps and independent record recovery phase="+args[0]);
    }
}
