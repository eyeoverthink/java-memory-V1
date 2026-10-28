package fraymus.media;

import com.google.gson.JsonObject;
import java.util.Arrays;

/** Standalone operator/proof entrypoint using the same runtime service as FraynixBoot. */
public final class MediaOutputCli {
    public static void main(String[] args) throws Exception {
        try (var media = MediaOutputService.shared()) {
            if (args.length > 1 && args[0].equals("--await")) {
                media.command("voice playback off");
                JsonObject options = new JsonObject();
                if (Boolean.getBoolean("fraynix.media.smoke")) {
                    options.addProperty("width", 256); options.addProperty("height", 256);
                    options.addProperty("steps", 8); options.addProperty("frames", 9);
                }
                String id = media.submit(args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)), new JsonObject(), options);
                var result = media.await(id, 3600);
                System.out.println(result);
                if (!result.get("status").getAsString().equals("completed")) throw new IllegalStateException("Media generation failed");
            } else {
                System.out.println(media.command(args.length == 0 ? "status" : String.join(" ", args)));
            }
        }
    }
}
