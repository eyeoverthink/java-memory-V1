package fraymus.genomics;

import fraymus.neural.AeonKronos;
import fraymus.knowledge.AkashicRecord;
import java.util.*;

/** Two processes prove quoted labels and every retained alias survive persistence. */
public final class KronosEscapedPersistenceTest {
    @SuppressWarnings("unchecked") public static void main(String[] args) throws Exception {
        System.setProperty("fraynix.kronos.bootstrap.enabled", "false");
        var kronos = AeonKronos.getInstance();
        String key = "LABEL \"QUOTED\" \\ PATH", value = "VALUE\nLINE";
        var field = AeonKronos.class.getDeclaredField("learnedAliases");
        field.setAccessible(true);
        var aliases = (Map<String, List<long[]>>) field.get(kronos);
        if (args[0].equals("seed")) {
            kronos.bind(key, value);
            long[] prototype = kronos.getConceptSpace().get(key);
            var retained = new ArrayList<long[]>();
            for (int i = 0; i < 120; i++) {
                long[] vector = prototype.clone(); vector[0] ^= (i + 1L); retained.add(vector);
            }
            aliases.put(key, retained);
        } else {
            if (!kronos.hasConcept(key) || kronos.bindIfAbsent(key, value))
                throw new AssertionError("Escaped concept/binding failed round trip");
            if (aliases.get(key) == null || aliases.get(key).size() != 120)
                throw new AssertionError("Retained aliases were truncated while saving/restoring");
        }
        kronos.shutdown(); new AkashicRecord().saveAll();
        System.out.println("PASS: escaped Kronos persistence phase=" + args[0]);
    }
}
