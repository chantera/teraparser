package jp.naist.cl.srparser.app;

import jp.naist.cl.srparser.io.Logger;

/**
 * jp.naist.cl.srparser.app
 *
 * @author Hiroki Teranishi
 */
public class Config {
    private static Config instance = null;
    private final Logger.LogLevel logLevel;
    private final boolean verbose;
    private final Integer iteration;
    private final String trainingFile;

    public enum Key {
        ITERATION,
        TRAINING_FILE;
    }

    private static <T> T get(Key key) {
        if (instance == null) {
            throw new IllegalStateException("Configuration must be initialiezed.");
        }
        if (key == Key.ITERATION) {
            return (T) instance.iteration;
        } else if (key == Key.TRAINING_FILE) {
            return (T) instance.trainingFile;
        }
        throw new IllegalArgumentException("Error: " + key + ": Config does not have such a key");
    }

    public static int getInt(Key key) {
        return Config.get(key);
    }

    public static String getString(Key key) {
        return Config.get(key);
    }

    private Config(Args args) {
        logLevel = args.logLevel;
        verbose = args.verbose;
        iteration = args.iteration;
        trainingFile = args.trainingFile;

        new Logger.Builder()
                .setLogLevel(logLevel)
                .setVerbose(verbose)
                .build();
    }

    public static void initialize(Args args) {
        if (instance == null) {
            instance = new Config(args);
        }
    }

    protected static class CmdLineArgs extends Args {

        protected CmdLineArgs(String[] args) {

        }
    }

    protected static class DebugArgs extends Args {

        protected DebugArgs() {
            super.logLevel = Logger.LogLevel.DEBUG;
            super.verbose = true;
            super.iteration = 20;
            super.trainingFile = "/Users/hiroki/Desktop/NLP/work/data/wsj_23.dev.conll";
        }
    }

    private abstract static class Args {
        private Logger.LogLevel logLevel;
        private boolean verbose;
        private int iteration;
        private String trainingFile;
    }
}
