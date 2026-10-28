package fraymus.specialists;

import java.io.*;
import java.util.Base64;

/** Local versioned brain capsules; only known state classes may be deserialized. */
final class BrainSnapshots {
    private BrainSnapshots() {}
    static String encode(Serializable brain) throws IOException {
        var bytes = new ByteArrayOutputStream();
        try(var output = new ObjectOutputStream(bytes)) { output.writeObject(brain); }
        return Base64.getEncoder().encodeToString(bytes.toByteArray());
    }
    static <T> T decode(String encoded, Class<T> type) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(encoded);
        try(var input = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            input.setObjectInputFilter(info -> {
                if(info.depth()>100 || info.references()>1_000_000 || info.streamBytes()>64L*1024*1024) return ObjectInputFilter.Status.REJECTED;
                Class<?> c=info.serialClass(); if(c==null)return ObjectInputFilter.Status.UNDECIDED;
                while(c.isArray())c=c.getComponentType(); String name=c.getName();
                return c.isPrimitive() || name.startsWith("fraymus.worldmodel.WorldModel") || name.startsWith("fraymus.embodied.EmbodiedAI")
                    || name.startsWith("java.util.") || name.startsWith("java.lang.") ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.REJECTED;
            });
            return type.cast(input.readObject());
        }
    }
}
