package fs;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Evolved_configgo {
    public interface Config {
        String architecture();
        String format(String... strings);
        long convertUin(String... uins);
        double convertFloat(String... floats);
        boolean convertBool(String... bools);

        String[] convertStrings(String... strs);
        int[] convertInts(String... ints);
        double[] convertFloats(String... floats);
        boolean[] convertBools(String... bools);

        int length();
        Stream\u003cString\u003e keys();
        Object value(String key);
    }
}