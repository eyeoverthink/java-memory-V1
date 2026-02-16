import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Evolved_cpufeatscpp {
    private static final int CPUID_BASE = 0;
    private static final int CPUID_EAX = 1;
    private static final int CPUID_ECX = 2;
    private static final int CPUID_EDX = 3;

    public boolean is_intel = false;
    public boolean is_amd = false;
    public byte[] vendor;
    public byte[] brand;
    public ByteBuffer f_1_ecx;
    public ByteBuffer f_1_edx;
    public ByteBuffer f_7_ebx;
    public ByteBuffer f_7_ecx;
    public ByteBuffer f_7_edx;
    public ByteBuffer f_7_1_eax;
    public ByteBuffer f_81_ecx;
    public ByteBuffer f_81_edx;

    public Evolved_cpufeatscpp() {
        cpuid(CPUID_BASE, 0);
        if (vendor == null) {
            vendor = new byte[0];
        }
    }

    public static native void cpuid(int subreg, int eax);

    public static native void print_cputype();

    public static native void main(String[] args);
}