package fraymus.brain.unified;

import fraymus.media.MediaOutputService;
import java.util.List;
import java.util.Map;

/** Output organ using the same job store for direct commands and orchestrated requests. */
public final class MediaOutputBrainAdapter extends AbstractBrainAdapter {
    public static final String NAME = "media_output";
    private final MediaOutputService media;
    public MediaOutputBrainAdapter(MediaOutputService media) {
        super(NAME, "Output", List.of(BrainCapability.AUDIO, BrainCapability.VISION, BrainCapability.CREATIVITY));
        this.media = media;
    }
    public static boolean accepts(BrainQuery query) {
        return query.getQuery().trim().matches("(?is)^(media|voice|dream)(\\s.*)?$");
    }
    @Override protected void onWire() {}
    @Override protected void onUnwire() {}
    @Override public BrainResponse process(BrainQuery query) {
        long start = System.currentTimeMillis();
        try {
            String result = media.command(query.getQuery());
            recordSuccess(System.currentTimeMillis() - start, 1, 0);
            return BrainResponse.success(NAME, result, 1, 0, Map.of("queryRoute", "MEDIA_OUTPUT", "completion", "Check job status; queued is not generated"), System.currentTimeMillis() - start);
        } catch (Exception error) {
            recordFailure();
            return BrainResponse.error(NAME, error.getMessage());
        }
    }
}
