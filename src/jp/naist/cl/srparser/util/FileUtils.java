package jp.naist.cl.srparser.util;

import java.io.File;

/**
 * jp.naist.cl.srparser.util
 *
 * @author Hiroki Teranishi
 */
public class FileUtils {
    public final static String SEPALATOR = System.getProperty("file.separator");

    private FileUtils() {
        throw new AssertionError();
    }

    public static boolean exists(String path) {
        return new File(path).exists();
    }

    public static boolean isReadable(String path) {
        return new File(path).canRead();
    }

    public static boolean isWritable(String path) {
        return new File(path).canWrite();
    }
}
