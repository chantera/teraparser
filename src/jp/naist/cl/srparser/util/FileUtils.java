package jp.naist.cl.srparser.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * jp.naist.cl.srparser.util
 *
 * @author Hiroki Teranishi
 */
public class FileUtils {
    public final static String SEPALATOR = System.getProperty("file.separator");
    public final static String GZIP_EXT = ".gz";

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

    public static void writeObject(String path, Object object, boolean gzip) throws IOException {
        ObjectOutputStream writer;
        if (gzip) {
            if (!path.endsWith(GZIP_EXT)) {
                path += GZIP_EXT;
            }
            FileOutputStream fos = new FileOutputStream(path);
            writer = new ObjectOutputStream(new GZIPOutputStream(fos));
        } else {
            FileOutputStream fos = new FileOutputStream(path);
            writer = new ObjectOutputStream(fos);
        }
        writer.writeObject(object);
        writer.close();
    }
}
