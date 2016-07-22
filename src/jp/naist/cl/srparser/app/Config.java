package jp.naist.cl.srparser.app;

import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.util.CmdLineArgs;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

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
        CONFIG_FILE      ("config",    null,                 false, ""),
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

    public static void initialize(String[] args) throws Exception {
        if (instance == null) {
            instance = new Config(args);
        }
    }

    private Config(String args[]) throws Exception {
        mode = load(new CmdLineArgs(args));
    }

    private App.Mode load(CmdLineArgs cmdArgs) throws IOException {
        App.Mode mode;
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
        Properties properties = new Properties();
        if (cmdArgs.hasOption(Key.CONFIG_FILE.name)) {
            properties.load(new FileReader(cmdArgs.getOption(Key.CONFIG_FILE.name)));
        }
        properties.putAll(cmdArgs.getOptions());
        putInt     (Key.ITERATION,        properties);
        putString  (Key.TRAINING_FILE,    properties);
        putString  (Key.DEVELOPMENT_FILE, properties);
        putLogLevel(Key.LOGLEVEL,         properties);
        putBoolean (Key.VERBOSE,          properties);
        System.out.println(properties);
        return mode;
    }

    private void putObject(Key key, Object value) {
        values.put(key, value);
    }

    private void putString(Key key, String value) {
        values.put(key, value);
    }

    private void putString(Key key, Properties properties) {
        putString(key, properties.getProperty(key.name, (String) key.defaultValue));
    }

    private void putInt(Key key, int value) {
        values.put(key, value);
    }

    private void putInt(Key key, Properties properties) {
        int value;
        if (properties.containsKey(key.name)) {
            value = Integer.parseInt(properties.getProperty(key.name));
        } else {
            value = (int) key.defaultValue;
        }
        putInt(key, value);
    }

    private void putBoolean(Key key, boolean value) {
        values.put(key, value);
    }

    private void putBoolean(Key key, Properties properties) {
        boolean value;
        if (properties.containsKey(key.name)) {
            String propValue = properties.getProperty(key.name);
            if (propValue == null) {
                value = true;
            } else {
                propValue = propValue.toLowerCase();
                value = propValue.equals("false") || propValue.equals("0");
            }
        } else {
            value = (boolean) key.defaultValue;
        }
        putBoolean(key, value);
    }

    private void putLogLevel(Key key, Properties properties) {
        Logger.LogLevel loglevel;
        String label = properties.getProperty(key.name, null);
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
            throw new IllegalArgumentException("Invalid Log Level");
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
        return (String) instance.values.get(key);
    }

    static int getInt(Key key) {
        checkInitialized();
        return (int) instance.values.get(key);
    }

    static boolean getBoolean(Key key) {
        checkInitialized();
        return (boolean) instance.values.get(key);
    }

    static boolean isSet(Key key) {
        checkInitialized();
        return instance.values.get(key) != null;
    }

    static void dump() {
    }

    static void showHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usage:\n");
    }
}
