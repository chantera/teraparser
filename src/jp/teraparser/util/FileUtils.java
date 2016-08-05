package jp.teraparser.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * jp.teraparser.util
 *
 * @author Hiroki Teranishi
 */
public class FileUtils {
    public static final String SEPALATOR = System.getProperty("file.separator");
    public static final String GZIP_EXT = ".gz";

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

    public static boolean isDirWritable(String path) {
        File file = new File(path);
        if (!file.isDirectory()) {
            file = file.getParentFile();
        }
        return file.canWrite();
    }

    public static Object readObject(String path) throws IOException, ClassNotFoundException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
        ObjectInputStream reader;
        if (isGZipped(bis)) {
            reader = new ObjectInputStream(new GZIPInputStream(bis));
        } else {
            reader = new ObjectInputStream(bis);
        }
        Object object = reader.readObject();
        reader.close();
        return object;
    }

    public static void writeObject(String path, Object object, boolean gzip) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
        ObjectOutputStream writer;
        if (gzip) {
            if (!path.endsWith(GZIP_EXT)) {
                path += GZIP_EXT;
            }
            writer = new ObjectOutputStream(new GZIPOutputStream(bos));
        } else {
            writer = new ObjectOutputStream(bos);
        }
        writer.writeObject(object);
        writer.close();
    }

    public static boolean isGZipped(InputStream in) {
        if (!in.markSupported()) {
            in = new BufferedInputStream(in);
        }
        in.mark(2);
        int magic = 0;
        try {
            magic = in.read() & 0xff | ((in.read() << 8) & 0xff00);
            in.reset();
        } catch (IOException e) {
            // e.printStackTrace(System.err);
            return false;
        }
        return magic == GZIPInputStream.GZIP_MAGIC;
    }

    public static boolean isGZipped(File f) {
        int magic = 0;
        try {
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            magic = raf.read() & 0xff | ((raf.read() << 8) & 0xff00);
            raf.close();
        } catch (Throwable e) {
            // e.printStackTrace(System.err);
            return false;
        }
        return magic == GZIPInputStream.GZIP_MAGIC;
    }
}
