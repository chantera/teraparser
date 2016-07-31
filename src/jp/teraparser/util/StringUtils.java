package jp.teraparser.util;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * jp.teraparser.util
 *
 * @author Hiroki Teranishi
 */
public class StringUtils {
    public static final String NEW_LINE = System.getProperty("line.separator");

    private StringUtils() {
        throw new AssertionError();
    }

    public static String[] rsplit(String str, String regex, int limit) {
        String[] split = str.split(regex);
        int length = split.length;
        if (length <= limit) {
            return split;
        }
        String[] result = new String[limit + 1];
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if ( i < length - limit ) {
                builder.append(split[i]);
            } else {
                result[i - (length - limit) + 1] = split[i];
            }
        }
        result[0] = builder.toString();
        return result;
    }

    public static String join(Object[] array, final String delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        Arrays.stream(array).forEach(element -> joiner.add((String) element));
        return joiner.toString();
    }

    public static String join(Object[] array, final char separator) {
        if (array == null) {
                return null;
            }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, char separator, final int startIndex, final int endIndex) {
        if (array == null) {
                return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String repeat(String str, int repeat) {
        return new String(new char[repeat]).replace("\0", str);
    }
}
