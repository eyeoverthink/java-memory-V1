package repl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Evolved_llamammaph {
    public static List\u003cbyte[]\u003e llamas;
    private static byte[] fileData = new byte[1024];
    private static byte[] mmapData = new byte[1024];

    public static void main(String[] args) throws Exception {
        createLlamas();
    }

    private static void createLlamas() throws Exception {
        List\u003cbyte[]\u003e llamas = new ArrayList\u003c\u003e();
        for (int i = 0; i \u003c 10; i++) {
            byte[] data = new byte[1024];
            int size = (int) (Math.random() * 1000);
            System.arraycopy(data, 0, fileData, 0, size);
            llamas.add(fileData.clone());
        }
    }

    public static class LlamaFile implements Cloneable {
        private String fileName;
        private long fileSize;

        public LlamaFile(String fileName, long fileSize) {
            this.fileName = fileName;
            this.fileSize = fileSize;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    public static class MMap implements Cloneable {
        private byte[] data;
        private int size;

        public MMap(byte[] data, int size) {
            this.data = data;
            this.size = size;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    public static class LLock implements Cloneable {
        private byte[] data;
        private int size;

        public LLock(byte[] data, int size) {
            this.data = data;
            this.size = size;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    public static class LlamaMap implements Cloneable {
        private byte[] data;
        private int size;

        public LlamaMap(byte[] data, int size) {
            this.data = data;
            this.size = size;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}