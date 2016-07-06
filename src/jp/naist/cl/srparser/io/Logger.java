package jp.naist.cl.srparser.io;

import jp.naist.cl.srparser.util.DateUtils;
import jp.naist.cl.srparser.util.StringUtils;
import jp.naist.cl.srparser.util.Tuple;

/**
 * jp.naist.cl.srparser.io
 *
 * @author Hiroki Teranishi
 */
public class Logger {
    public final static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.S z";
    public final static String DEFAULT_LOG_FORMAT = "%(time)\t%(accessid)\t[%(level)]\t%(message)";
    public final static String DEFAULT_FILE_FORMAT = "yyyy-MM-dd";
    private static Logger instance = null;
    private final long accessUnixTime;
    private final String accessId;
    private final String accessTime;
    private final LogLevel logLevel;
    private final Boolean verbose;
    private final String dir;
    private final String logFormat;
    private final String fileFormat;

    public enum LogLevel {
        OFF(    Tuple.create(0b0000_0000_0000_0000_0000_0000, "off"   )),
        ERROR(  Tuple.create(0b0000_0000_0000_0000_0000_0001, "error" )),
        WARNING(Tuple.create(0b0000_0000_0000_0000_0000_0010, "warn"  )),
        NOTICE( Tuple.create(0b0000_0000_0000_0000_0000_1000, "notice")),
        INFO(   Tuple.create(0b0001_0000_0000_0000_0000_0000, "info"  )),
        DEBUG(  Tuple.create(0b0010_0000_0000_0000_0000_0000, "debug" )),
        TRACE(  Tuple.create(0b0100_0000_0000_0000_0000_0000, "trace" )),
        ALL(    Tuple.create(0b1111_1111_1111_1111_1111_1111, "all"   ));

        private final int value;
        private final String label;

        LogLevel(Tuple<Integer, String> definition) {
            this.value = definition.getLeft();
            this.label = definition.getRight();
        }

        public int getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }

        public boolean isPriorTo(LogLevel o) {
            return this.value >= o.value;
        }

        public boolean isPriorTo(int value) {
            return this.value >= value;
        }
    }

    public static Logger getInstence() {
        if (instance == null) {
            throw new IllegalStateException("Logger must be built once.");
        }
        return instance;
    }

    private Logger(Builder builder) {
        this.accessUnixTime = DateUtils.getTimeInMillis();
        this.accessId = StringUtils.generateHexId();
        this.accessTime = DateUtils.getDateTimeString(TIME_FORMAT, accessUnixTime);
        this.logLevel = builder.logLevel;
        this.verbose = builder.verbose;
        this.dir = builder.dir;
        this.logFormat = builder.logFormat;
        this.fileFormat = builder.fileFormat;
        instance = this;
        start();
    }

    public static void terminate() {
        instance.stop();
        instance = null;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
        } finally {
            stop();
        }
    }

    private void start() {
        log(LogLevel.INFO, "LOG Start with ACCESSID=%s ACCESSTIME=%s", accessId, accessTime);
    }

    private void stop() {
        double processTime = (double) (DateUtils.getTimeInMillis() - accessUnixTime) / 1000;
        log(LogLevel.INFO, "LOG End with ACCESSID=%s ACCESSTIME=%s PROCESSTIME=[%3.6f]", accessId, accessTime, processTime);
    }

    private String buildLogOutput(LogLevel loglevel, String message) {
        String time = DateUtils.getCurrentDateTimeString(TIME_FORMAT);
        String level = loglevel.getLabel();
        String output = logFormat;
        output = output.replaceAll("%\\(time\\)", time);
        output = output.replaceAll("%\\(accessid\\)", accessId);
        output = output.replaceAll("%\\(level\\)", level);
        output = output.replaceAll("%\\(message\\)", message);
        return output;
    }

    private void printLog(LogLevel logLevel, String message) {
        String output = buildLogOutput(logLevel, message);
        if (this.logLevel.isPriorTo(logLevel)) {
            // @TODO implement
            System.out.println("[TODO: this should be written in a log file.]");
        }
        if (verbose) {
            if (LogLevel.NOTICE.isPriorTo(logLevel)) {
                System.err.println(output);
            } else {
                System.out.println(output);
            }
        }
    }

    public static void log(LogLevel logLevel, String format, Object... args) {
        log(logLevel, String.format(format, args));
    }

    public static void log(LogLevel logLevel, String message) {
        getInstence().printLog(logLevel, message);
    }

    public static void log(LogLevel logLevel, Object object) {
        log(logLevel, object.toString());
    }

    public static class Builder {
        private LogLevel logLevel = LogLevel.WARNING;
        private Boolean verbose = false;
        private String dir = "";
        private String logFormat = DEFAULT_LOG_FORMAT;
        private String fileFormat = DEFAULT_FILE_FORMAT;

        public Logger build() {
            return new Logger(this);
        }

        public Builder setLogLevel(LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public Builder setVerbose(Boolean verbose) {
            this.verbose = verbose;
            return this;
        }

        public Builder setOutputDir(String dir) {
            this.dir = dir;
            return this;
        }

        public Builder setLogFormat(String format) {
            this.logFormat = format;
            return this;
        }

        public Builder setFileFormat(String format) {
            this.fileFormat = format;
            return this;
        }
    }

    public static void error(String format, Object... args) {
        log(LogLevel.ERROR, format, args);
    }

    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public static void error(Object object) {
        log(LogLevel.ERROR, object);
    }

    public static void warning(String format, Object... args) {
        log(LogLevel.WARNING, format, args);
    }

    public static void warning(String message) {
        log(LogLevel.WARNING, message);
    }

    public static void warning(Object object) {
        log(LogLevel.WARNING, object);
    }

    public static void notice(String format, Object... args) {
        log(LogLevel.INFO, format, args);
    }

    public static void notice(String message) {
        log(LogLevel.INFO, message);
    }

    public static void notice(Object object) {
        log(LogLevel.INFO, object);
    }

    public static void info(String format, Object... args) {
        log(LogLevel.INFO, format, args);
    }

    public static void info(String message) {
        log(LogLevel.INFO, message);
    }

    public static void info(Object object) {
        log(LogLevel.INFO, object);
    }

    public static void debug(String format, Object... args) {
        log(LogLevel.DEBUG, format, args);
    }

    public static void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public static void debug(Object object) {
        log(LogLevel.DEBUG, object);
    }

    public static void trace(String format, Object... args) {
        log(LogLevel.TRACE, format, args);
    }

    public static void trace(String message) {
        log(LogLevel.TRACE, message);
    }

    public static void trace(Object object) {
        log(LogLevel.TRACE, object);
    }
}