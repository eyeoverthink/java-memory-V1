package cpu;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Evolved_cpudebuggo {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024);
        buffer.order(ByteOrder.nativeOrder());
        buffer.put(new byte[3]); // Replace the escaped character with a new byte
        buffer.flip();
        System.out.println(buffer.array());
    }
}