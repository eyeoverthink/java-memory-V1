package repl;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class Evolved_parsersgo {

    public interface Parser {
        Object[] Init(Object tools, Object lastMessage, Object thinkValue);
        Object[] Add(String s, boolean done);
        boolean HasToolSupport();
        boolean HasThinkingSupport();
    }

    public abstract class BaseParser implements Parser {
        @Override
        public Object[] Init(Object tools, Object lastMessage, Object thinkValue) {
            return (Object[])tools;
        }

        @Override
        public Object[] Add(String s, boolean done) {
            return new Object[]{s};
        }
    }
}