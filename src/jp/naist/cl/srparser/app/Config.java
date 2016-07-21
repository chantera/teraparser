package jp.naist.cl.srparser.app;

import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.util.CmdLineArgs;

import java.util.HashMap;

/**
 * jp.naist.cl.srparser.app
 *
 * @author Hiroki Teranishi
 */
public class Config {
    private static Config instance = null;
    private final App.Mode mode;
    private HashMap<Key, Object> values = new HashMap<>();

    enum Key {
        ITERATION        ("iteration", 20,                   false, ""),
        TRAINING_FILE    ("trainfile", "",                   true,  ""),
        DEVELOPMENT_FILE ("devfile",   "",                   false, ""),
        VERBOSE          ("verbose",   true,                 false, ""),
        LOGLEVEL         ("loglevel",  Logger.LogLevel.INFO, false, "");

        String name;
        Object defaultValue;
        Boolean required;
        String description;

        Key(String name, Object defaultValue, Boolean required, String description) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.required = required;
            this.description = description;
        }
    }

    public static void initialize(String[] args) {
        if (instance == null) {
            instance = new Config(args);
        }
    }

    private Config(String args[]) {
        CmdLineArgs cmdArgs = new CmdLineArgs(args);
        String command = cmdArgs.getParamOrDefalut(0, "");
        if (command.equals(App.Mode.PARSE.label)) {
            mode = App.Mode.PARSE;
        } else if (command.equals(App.Mode.TRAIN.label)) {
            mode = App.Mode.TRAIN;
        } else {
            mode = null;
            System.err.println("Invalid Command: " + command);
            showHelp();
            System.exit(0); // would not proceed to App.execute()
        }
        processCommandLineArgs(cmdArgs);
    }

    private void processCommandLineArgs(CmdLineArgs cmdArgs) {
        putInt     (Key.ITERATION,        cmdArgs);
        putString  (Key.TRAINING_FILE,    cmdArgs);
        putString  (Key.DEVELOPMENT_FILE, cmdArgs);
        putLogLevel(Key.LOGLEVEL,         cmdArgs);
        putBoolean (Key.VERBOSE,          cmdArgs);
    }

    private void putObject(Key key, Object value) {
        values.put(key, value);
    }

    private void putString(Key key, String value) {
        values.put(key, value);
    }

    private void putString(Key key, CmdLineArgs cmdArgs) {
        String value;
        if (cmdArgs.hasOption(key.name)) {
            value = cmdArgs.getOption(key.name);
        } else {
            value = key.defaultValue != null ? key.defaultValue.toString() : null;
        }
        putString(key, value);
    }

    private void putInt(Key key, int value) {
        values.put(key, value);
    }

    private void putInt(Key key, CmdLineArgs cmdArgs) {
        int value;
        if (cmdArgs.hasOption(key.name)) {
            value = Integer.parseInt(cmdArgs.getOption(key.name));
        } else {
            value = (int) key.defaultValue;
        }
        putInt(key, value);
    }

    private void putBoolean(Key key, boolean value) {
        values.put(key, value);
    }

    private void putBoolean(Key key, CmdLineArgs cmdArgs) {
        putBoolean(key, cmdArgs.hasOption(key.name) || (boolean) key.defaultValue);
    }

    private void putLogLevel(Key key, CmdLineArgs cmdArgs) {
        Logger.LogLevel loglevel;
        String label = cmdArgs.getOptionOrDefault(key.name, null);
        if (label == null) {
            loglevel = (Logger.LogLevel) key.defaultValue;
        } else if (label.equals(Logger.LogLevel.OFF.getLabel())) {
            loglevel = Logger.LogLevel.OFF;
        } else if (label.equals(Logger.LogLevel.ERROR.getLabel())) {
            loglevel = Logger.LogLevel.ERROR;
        } else if (label.equals(Logger.LogLevel.WARNING.getLabel())) {
            loglevel = Logger.LogLevel.WARNING;
        } else if (label.equals(Logger.LogLevel.NOTICE.getLabel())) {
            loglevel = Logger.LogLevel.NOTICE;
        } else if (label.equals(Logger.LogLevel.INFO.getLabel())) {
            loglevel = Logger.LogLevel.INFO;
        } else if (label.equals(Logger.LogLevel.DEBUG.getLabel())) {
            loglevel = Logger.LogLevel.DEBUG;
        } else if (label.equals(Logger.LogLevel.TRACE.getLabel())) {
            loglevel = Logger.LogLevel.TRACE;
        } else if (label.equals(Logger.LogLevel.ALL.getLabel())) {
            loglevel = Logger.LogLevel.ALL;
        } else {
            throw new IllegalArgumentException();
        }
        putObject(key, loglevel);
    }

    private static boolean checkInitialized() {
        if (instance == null) {
            throw new IllegalStateException("Configuration must be initialiezed.");
        }
        return true;
    }

    static App.Mode getMode() {
        checkInitialized();
        return instance.mode;
    }

    static Object getObject(Key key) {
        checkInitialized();
        return instance.values.get(key);
    }

    static String getString(Key key) {
        checkInitialized();
        Object value = instance.values.get(key);
        return value != null ? value.toString() : null;
    }

    static int getInt(Key key) {
        checkInitialized();
        return (int) instance.values.get(key);
    }

    static boolean getBoolean(Key key) {
        checkInitialized();
        return (boolean) instance.values.get(key);
    }

    // private void loadConfigFile(String filepath) {}

    /*
    private static <T> T get(Key key) {
        if (instance == null) {
            throw new IllegalStateException("Configuration must be initialiezed.");
        }
        if (key == Key.ITERATION) {
            return (T) instance.iteration;
        } else if (key == Key.TRAINING_FILE) {
            return (T) instance.trainingFile;
        } else if (key == Key.DEVELOPMENT_FILE) {
            return (T) instance.developmentFile;
        }
        throw new IllegalArgumentException("Error: " + key + ": Config does not have such a key");
    }

    static int getInt(Key key) {
        return Config.get(key);
    }

    static String getString(Key key) {
        return Config.get(key);
    }
    */

    /*
    private Config(Args args) {
        logLevel = args.logLevel;
        verbose = args.verbose;
        iteration = args.iteration;
        trainingFile = args.trainingFile;
        developmentFile = args.developmentFile;

        new Logger.Builder()
                .setLogLevel(logLevel)
                .setVerbose(verbose)
                .build();
    }
    */

    /*
    protected static class CmdLineArgs extends Args {

        protected CmdLineArgs(String[] args) {

        }
    }

    protected static class DebugArgs extends Args {

        protected DebugArgs() {
            super.logLevel = Logger.LogLevel.DEBUG;
            super.verbose = true;
            super.iteration = 20;
            // super.trainingFile = "/Users/hiroki/Desktop/NLP/data/wsj_02.half.conll";
            super.trainingFile = "/Users/hiroki/Desktop/NLP/data/penn_conll/wsj_02.conll";
            // super.trainingFile = "/Users/hiroki/Desktop/NLP/data/wsj_23.dev.conll";
            super.developmentFile = "/Users/hiroki/Desktop/NLP/data/wsj_23.dev.conll";
            // super.trainingFile = "/Users/hiroki/Desktop/NLP/work/data/wsj_02-21.conll";
            // super.developmentFile = "/Users/hiroki/Desktop/NLP/work/data/penn_conll/wsj_22.conll";
        }
    }

    private abstract static class Args {
        private Logger.LogLevel logLevel;
        private boolean verbose;
        private int iteration;
        private String trainingFile;
        private String developmentFile;
    }
    */

    static void dump() {
    }

    static void showHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usage:\n");
    }
}
