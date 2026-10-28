package fraymus.runtime;

import fraymus.os.FrayFSBuilder;

public final class ExodusInvocationHelper {
    private ExodusInvocationHelper() {
    }

    public static void main(String[] args) {
        new FrayFSBuilder().executeExodus();
    }
}
