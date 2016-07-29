package jp.naist.cl.srparser.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * jp.naist.cl.srparser.util
 *
 * @author Hiroki Teranishi
 */
public class DateUtils {
    private static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final Calendar calendar = Calendar.getInstance();

    private DateUtils() {
        throw new AssertionError();
    }

    private static class SafeDateFormat {
        private static final ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>(){
            @Override
            protected SimpleDateFormat initialValue()
            {
                return new SimpleDateFormat(DEFAULT_TIME_FORMAT);
            }
        };

        private SafeDateFormat() {}

        public static final void applyPattern(String pattern) {
            formatter.get().applyPattern(pattern);
        }

        public static final String format(Date date) {
            return formatter.get().format(date);
        }

        public static final Date parse(final String source, final ParsePosition pos) {
            return formatter.get().parse(source, pos);
        }

        public static final Date parse(String source) throws ParseException {
            return formatter.get().parse(source);
        }

        public static final void setLenient(boolean lenient) {
            formatter.get().setLenient(lenient);
        }
    }

    public static long getGmtTimeInMillis() {
        long offset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
        return getTimeInMillis() - offset;
    }

    public static long getTimeInMillis() {
        return System.currentTimeMillis();
    }

    public static String getCurrentDateTimeString() {
        return getDateTimeString(new Date());
    }

    public static String getCurrentDateTimeString(String format) {
        return getDateTimeString(format, new Date());
    }

    public static String getDateTimeString(long date) {
        return getDateTimeString(new Date(date));
    }

    public static String getDateTimeString(String format, long date) {
        return getDateTimeString(format, new Date(date));
    }

    public static String getDateTimeString(Date date) {
        return getDateTimeString(DEFAULT_TIME_FORMAT, date);
    }

    public static String getDateTimeString(String format, Date date) {
        SafeDateFormat.applyPattern(format);
        return SafeDateFormat.format(date);
    }
}
