package jp.naist.cl.srparser.app;

import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.util.CmdLineArgs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;

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
        TRAIN_LOCALLY    ("locally",   false,                false, ""),
        BEAM_WIDTH       ("beamwidth", 16,                   false, ""),
        EARLY_UPDATE     ("early",     false,                false, ""),
        VERBOSE          ("verbose",   true,                 false, ""),
        LOGDIR           ("logdir",    "logs",               false, ""),
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
        mode = load(new CmdLineArgs(args));
    }

    private App.Mode load(CmdLineArgs cmdArgs) {
        App.Mode mode;
        String command = cmdArgs.getParamOrDefalut(0, "");
        if (command.equals(App.Mode.PARSE.label)) {
            mode = App.Mode.PARSE;
        } else if (command.equals(App.Mode.TRAIN.label)) {
            mode = App.Mode.TRAIN;
        } else if (command.equals(App.Mode.HELP.label)) {
            return App.Mode.HELP; // this does not read any more args.
        } else {
            System.err.println("Invalid Command: " + command);
            return App.Mode.HELP; // this does not read any more args.
        }
        Properties properties = new Properties();
        // Key.CONFIG_FILE can be specified only by args
        if (cmdArgs.hasOption(Key.CONFIG_FILE.name)) {
            String configFilepath = cmdArgs.getOption(Key.CONFIG_FILE.name);
            try {
                properties.load(new FileReader(configFilepath));
            } catch (FileNotFoundException e) {
                System.err.println(Key.CONFIG_FILE.name + "=" + configFilepath + " Not Found.");
                return App.Mode.NONE; // this stop reading args anymore.
            } catch (IOException e) {
                System.err.println("Cannot read " + Key.CONFIG_FILE.name + "=" + configFilepath + ".");
                e.printStackTrace();
                return App.Mode.NONE; // this stop reading args anymore.
            }
            putString(Key.CONFIG_FILE, configFilepath);
        }
        properties.putAll(cmdArgs.getOptions()); // override values by command line args
        putInt     (Key.ITERATION,        properties);
        putString  (Key.TRAINING_FILE,    properties);
        putString  (Key.DEVELOPMENT_FILE, properties);
        putBoolean (Key.TRAIN_LOCALLY,    properties);
        putInt     (Key.BEAM_WIDTH,       properties);
        putBoolean (Key.EARLY_UPDATE,     properties);
        putString  (Key.LOGDIR,           properties);
        putLogLevel(Key.LOGLEVEL,         properties);
        putBoolean (Key.VERBOSE,          properties);
        // System.out.println(properties);
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
                value = true; // this is because the key is specified but not explicitly disabled.
            } else {
                propValue = propValue.toLowerCase();
                value = !(propValue.equals("false") || propValue.equals("0"));
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

    static String getDump() {
        checkInitialized();
        StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<Key, Object> e : instance.values.entrySet()) {
            joiner.add(e.getKey().name + "=" + e.getValue());
        }
        return "{" + joiner.toString() + "}";
    }

    static void showHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usage:\n");
        System.out.println(sb.toString());
    }
}
