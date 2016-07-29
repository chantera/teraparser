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

    public static class HashCodeBuilder {
        private static final int SEED = 17;
        private static final int PRIME = 31;

        private int seed;

        public HashCodeBuilder() {
            this.seed = SEED;
        }

        public HashCodeBuilder append(boolean value) {
            seed = PRIME * seed + (value ? 1 : 0);
            return this;
        }

        public HashCodeBuilder append(boolean[] values) {
            for (boolean value : values) {
                append(value);
            }
            return this;
        }

        public HashCodeBuilder append(byte value) {
            seed = PRIME * seed + (int) value;
            return this;
        }

        public HashCodeBuilder append(byte[] values) {
            for (byte value : values) {
                append(value);
            }
            return this;
        }

        public HashCodeBuilder append(char value) {
            seed = PRIME * seed + (int) value;
            return this;
        }

        public HashCodeBuilder append(char[] values) {
            for (char value : values) {
                append(value);
            }
            return this;
        }

        public HashCodeBuilder append(short value) {
            seed = PRIME * seed + (int) value;
            return this;
        }

        public HashCodeBuilder append(short[] values) {
            for (short value : values) {
                append(value);
            }
            return this;
        }

        public HashCodeBuilder append(int value) {
            seed = PRIME * seed + value;
            return this;
        }

        public HashCodeBuilder append(int[] values) {
            for (int value : values) {
                append(value);
            }
            return this;
        }

        public HashCodeBuilder append(long value) {
            seed = PRIME * seed + (int) (value ^ (value >>> 32));
            return this;
        }

        public HashCodeBuilder append(long[] values) {
            for (long value : values) {
                append(value);
            }
            return this;
        }

        public HashCodeBuilder append(float value) {
            seed = PRIME * seed + Float.floatToIntBits(value);
            return this;
        }

        public HashCodeBuilder append(float[] values) {
            for (float value : values) {
                append(value);
            }
            return this;
        }

        public HashCodeBuilder append(double value) {
            return append(Double.doubleToLongBits(value));
        }

        public HashCodeBuilder append(double[] values) {
            for (double value : values) {
                append(value);
            }
            return this;
        }

        public HashCodeBuilder append(Object value) {
            if (value == null) {
                seed = PRIME * seed;
                return this;
            }
            if (value.getClass().isArray()) {
                if (value instanceof boolean[]) {
                    append((boolean[]) value);
                } else if (value instanceof byte[]) {
                    append((byte[]) value);
                } else if (value instanceof char[]) {
                    append((char[]) value);
                } else if (value instanceof short[]) {
                    append((short[]) value);
                } else if (value instanceof int[]) {
                    append((int[]) value);
                } else if (value instanceof long[]) {
                    append((long[]) value);
                } else if (value instanceof float[]) {
                    append((float[]) value);
                } else if (value instanceof double[]) {
                    append((double[]) value);
                } else {
                    append((Object[]) value);
                }
            } else {
                if (value instanceof Boolean) {
                    append(((Boolean) value).booleanValue());
                } else if (value instanceof Byte) {
                    append(((Byte) value).byteValue());
                } else if (value instanceof Character) {
                    append(((Character) value).charValue());
                } else if (value instanceof Short) {
                    append(((Short) value).shortValue());
                } else if (value instanceof Integer) {
                    append(((Integer) value).intValue());
                } else if (value instanceof Long) {
                    append(((Long) value).longValue());
                } else if (value instanceof Float) {
                    append(((Float) value).floatValue());
                } else if (value instanceof Double) {
                    append(((Double) value).doubleValue());
                } else {
                    seed = PRIME * seed + value.hashCode();
                }
            }
            return this;
        }

        public HashCodeBuilder append(Object[] values) {
            for (Object value : values) {
                append(value);
            }
            return this;
        }

        public int toHashCode() {
            return seed;
        }

        public int build() {
            return toHashCode();
        }

        @Override
        public int hashCode() {
            return toHashCode();
        }
    }
}
