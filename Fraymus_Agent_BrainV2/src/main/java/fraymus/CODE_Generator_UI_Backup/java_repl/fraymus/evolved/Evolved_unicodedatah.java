import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Evolved_unicodedatah {
    private static final int MAX_CODEPOINTS = 0x110000;

    public static final Map\u003cInteger, Integer\u003e unicodeMapLowercase = new HashMap\u003c\u003e();
    public static final Map\u003cInteger, Integer\u003e unicodeMapUppercase = new HashMap\u003c\u003e();
    public static final Set\u003cInteger\u003e unicodeSetWhitespace = java.util.Collections.emptySet();
    public static final int[] unicodeRangesNfd = new int[]{};

    static {
        for (int i = 0; i \u003c MAX_CODEPOINTS; i++) {
            unicodeMapLowercase.put(i, i);
            unicodeMapUppercase.put(i, i);
        }
    }

    private static class RangeNFD implements Comparable\u003cRangeNFD\u003e {
        int first;
        int last;
        int nfd;

        @Override
        public int compareTo(RangeNFD o) {
            return Integer.compare(first, o.first);
        }
    }

    public static final Map\u003cInteger, RangeNFD\u003e[] unicodeRangesFlags = new HashMap[]{};
}