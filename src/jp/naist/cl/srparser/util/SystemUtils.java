package jp.naist.cl.srparser.util;

/**
 * jp.naist.cl.srparser.util
 *
 * @author Hiroki Teranishi
 */
public class SystemUtils {
    private SystemUtils() {
        throw new AssertionError();
    }

    public static final long KILOBYTE = 1024L;
    public static final long MEGABYTE = 1024L * 1024L;

    private static Runtime runtime = Runtime.getRuntime();

    public static String getOSName() {
        return getProperty("os.name");
    }

    public static String getOSVersion() {
        return getProperty("os.version");
    }

    public static String getOSArch() {
        return getProperty("os.arch");
    }

    public static String getOSInfo() {
        return String.format("OS: %s, Version: %s, Architecture: %s", getOSName(), getOSVersion(), getOSArch());
    }

    public static String getProperty(String key) {
        return System.getProperty(key);
    }

    public static long getFreeMemory() {
        return runtime.freeMemory();
    }

    public static double getFreeMemoryMB() {
        return (double) getFreeMemory() / MEGABYTE;
    }

    public static long getUsedMemory() {
        return getTotalMemory() - getFreeMemory();
    }

    public static double getUsedMemoryMB() {
        return (double) getUsedMemory() / MEGABYTE;
    }

    public static long getTotalMemory() {
        return runtime.totalMemory();
    }

    public static double getTotalMemoryMB() {
        return (double) getTotalMemory() / MEGABYTE;
    }

    public static long getMaxMemory() {
        return runtime.maxMemory();
    }

    public static double getMaxMemoryMB() {
        return (double) getMaxMemory() / MEGABYTE;
    }
}
