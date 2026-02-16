package repl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Evolved_llamaioh {
    public interface IWriteStream {
        void write(byte[] src, int size);

        void writeTensor(long[] tensor, int offset, int size);

        int nBytesWritten();
    }

    public interface IReadStream {
        byte[] read(int size);

        void readTo(byte[] dst, int size);

        int nBytesRead();
    }

    private static class StringReader implements IReadStream {
        private final StringBuilder buffer = new StringBuilder();

        @Override
        public byte[] read(int size) {
            return buffer.toString().getBytes();
        }

        @Override
        public void readTo(byte[] dst, int size) {
            buffer.setLength(Math.min(size, buffer.length()));
            System.arraycopy(buffer.toString().getBytes(), 0, dst, 0, buffer.length());
        }

        @Override
        public int nBytesRead() {
            return buffer.length();
        }
    }

    private static class StringWriter implements IWriteStream {
        private final List\u003cbyte[]\u003e buffers = new ArrayList\u003c\u003e();

        @Override
        public void write(byte[] src, int size) {
            buffers.add(ByteBuffer.wrap(src).array());
        }

        @Override
        public void writeTensor(long[] tensor, int offset, int size) {

        }

        @Override
        public int nBytesWritten() {
            return buffers.stream()
                    .mapToInt(b -\u003e b.length)
                    .sum();
        }
    }

    private StringReader reader = new StringReader();

    private StringWriter writer = new StringWriter();

    public Evolved_llamaioh(String str) {
        this.reader = new StringReader();
        this.reader.buffer.append(str);
    }

    public byte[] read(int size) {
        return reader.read(size);
    }

    public void readTo(byte[] dst, int size) {
        reader.readTo(dst, size);
    }

    public int nBytesRead() {
        return reader.nBytesRead();
    }

    public Evolved_llamaioh write(byte[] src, int size) {
        writer.write(src, size);
        return this;
    }

    public Evolved_llamaioh tensor(long[] tensor, int offset, int size) {
        writer.writeTensor(tensor, offset, size);
        return this;
    }

    public Evolved_llamaioh flush() {
        String s = reader.buffer.toString();
        return new Evolved_llamaioh(s);
    }
}