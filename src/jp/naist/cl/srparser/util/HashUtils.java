package jp.naist.cl.srparser.util;

import java.util.UUID;

/**
 * jp.naist.cl.srparser.util
 *
 * @author Hiroki Teranishi
 */
public class HashUtils {
    private HashUtils() {
        throw new AssertionError();
    }

    public static String generateHexId() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    /**
     * Jenkins's one-at-a-time hash
     */
    public static int oneAtATimeHash(int[] key) {
        int hash = 0;
        for (int v : key) {
            hash += v;
            hash += (hash << 10);
            hash ^= (hash >> 6);
        }
        hash += (hash << 3);
        hash ^= (hash >> 11);
        hash += (hash << 15);
        return hash;
    }
}
