package x86;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Evolved_x86go {
    public static native int hamming_weight(long n);

    public static native ByteBuffer encode_varint(int value);
    public static native long decode_varint(ByteBuffer varint);

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putLong(0x1234567890abcdefL);
        System.out.println(buffer); // Removed backslash
    }
}