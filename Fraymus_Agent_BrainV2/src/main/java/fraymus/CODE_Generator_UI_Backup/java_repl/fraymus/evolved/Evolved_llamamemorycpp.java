package repl;

import java.util.EnumSet;

public class Evolved_llamamemorycpp {

    public enum LlamaMemoryStatus {
        SUCCESS,
        NO_UPDATE,
        FAILED_PREPARE,
        FAILED_COMPUTE
    }

    private static final EnumSet\u003cLlamaMemoryStatus\u003e FAILS = EnumSet.of(LlamaMemoryStatus.FAILED_PREPARE, LlamaMemoryStatus.FAILED_COMPUTE);

    public static LlamaMemoryStatus combine(LlamaMemoryStatus s0, LlamaMemoryStatus s1) {
        boolean hasUpdate = false;

        switch (s0) {
            case SUCCESS:
                hasUpdate = true;
                break;
            default:
                if (!FAILS.contains(s0)) {
                    return s0;
                }
        }

        switch (s1) {
            case SUCCESS:
                hasUpdate = true;
                break;
            default:
                if (!FAILS.contains(s1)) {
                    return s1;
                }
        }

        return hasUpdate ? LlamaMemoryStatus.SUCCESS : LlamaMemoryStatus.NO_UPDATE;
    }

    public static boolean isFail(LlamaMemoryStatus status) {
        switch (status) {
            case SUCCESS:
            case NO_UPDATE:
                return false;
            default:
                return true;
        }
    }

    public enum CombinedLlamaMemoryStatus {
        UPDATE,
        NO_UPDATE
    }

    public static class Evolved_LlamaMemoryStatus {
        public static CombinedLlamaMemoryStatus combined(LlamaMemoryStatus s0, LlamaMemoryStatus s1) {
            boolean hasUpdate = false;

            switch (s0) {
                case SUCCESS:
                    hasUpdate = true;
                    break;
                case NO_UPDATE:
                    break;
                default:
                    if (!FAILS.contains(s0)) {
                        return CombinedLlamaMemoryStatus.NO_UPDATE;
                    }
            }

            switch (s1) {
                case SUCCESS:
                    hasUpdate = true;
                    break;
                case NO_UPDATE:
                    break;
                default:
                    if (!FAILS.contains(s1)) {
                        return CombinedLlamaMemoryStatus.NO_UPDATE;
                    }
            }

            return hasUpdate ? CombinedLlamaMemoryStatus.UPDATE : CombinedLlamaMemoryStatus.NO_UPDATE;
        }
    }

    public static void main(String[] args) {
        System.out.println(Evolved_LlamaMemoryStatus.combined(LlamaMemoryStatus.SUCCESS, LlamaMemoryStatus.NO_UPDATE));  
        System.out.println(Evolved_LlamaMemoryStatus.combined(LlamaMemoryStatus.FAILED_PREPARE, LlamaMemoryStatus.FAILED_COMPUTE));
    }
}