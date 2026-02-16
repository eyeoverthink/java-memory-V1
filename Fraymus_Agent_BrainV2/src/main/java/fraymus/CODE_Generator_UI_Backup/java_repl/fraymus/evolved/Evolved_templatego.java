package repl;

import java.util.function.Predicate;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Evolved_templatego {
    public static void main(String[] args) {

    }

    public static \u003cT\u003e List\u003cT\u003e filterList(List\u003cT\u003e list, Predicate\u003cT\u003e predicate) {
        return list.stream().filter(predicate).collect(Collectors.toList());
    }
}