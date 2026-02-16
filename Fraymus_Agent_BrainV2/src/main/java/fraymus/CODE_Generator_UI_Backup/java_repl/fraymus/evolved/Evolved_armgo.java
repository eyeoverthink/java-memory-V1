package arm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Evolved_armgo {
    public static native void init();
    public static native long execute(long[] data, int length);

    public static void main(String[] args) {
        init();
        long[] data = new long[1024];
        for (int i = 0; i \u003c 1024; i++) {
            data[i] = (long)i;
        }
        System.out.println(Arrays.toString(data));
    }

    // removed native keyword from method definition
}