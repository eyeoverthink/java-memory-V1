package com.example;

import java.util.Arrays;
import java.util.List;

public class Evolved_ollamadebugh {

    public static void ollamaDebug(List\u003cDouble\u003e tensor, boolean verbose) {
        if (tensor == null || !tensor.stream().allMatch(x -\u003e Double.isNaN(x))) {
            throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) {
        List\u003cDouble\u003e tensor = Arrays.asList(1.0, 2.0, 3.0);
        ollamaDebug(tensor, true);
    }
}