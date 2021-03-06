package jp.teraparser.app;

import jp.teraparser.Main;
import jp.teraparser.io.Logger;
import jp.teraparser.util.CmdLineArgs;
import jp.teraparser.util.DateUtils;
import jp.teraparser.util.FileUtils;
import jp.teraparser.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;

/**
 * jp.teraparser.app
 *
 * @author Hiroki Teranishi
 */
public class Config {
    private static final String CONFIG_FILE_EXT = ".properties";
    private static final String DEFAULT_CONFIG_DIR = "." + FileUtils.SEPALATOR + "config";
    private static final String DEFAULT_NEW_CONFIG_FILENAME_FORMAT = "yyyy-MM-dd";
    private static Config instance = null;
    private final App.Mode mode;
    private Map<Key, Object> values = new EnumMap<>(Key.class);

    enum Key {
        CONFIG_FILE      ("config",     null                ),
        SAVE_CONFIG      ("saveconfig", null                ),
        INPUT            ("input",      ""                  ),
        MODEL_INPUT      ("modelin",    ""                  ),
        TRAINING_FILE    ("trainfile",  ""                  ),
        DEVELOPMENT_FILE ("devfile",    ""                  ),
        TEST_FILE        ("testfile",   ""                  ),
        MODEL_OUTPUT     ("modelout",   ""                  ),
        ITERATION        ("iteration",  20                  ),
        // N_THREADS        ("nthreads",   8                   ),
        TRAIN_LOCALLY    ("locally",    false               ),
        BEAM_WIDTH       ("beamwidth",  16                  ),
        EARLY_UPDATE     ("early",      false               ),
        VERBOSE          ("verbose",    true                ),
        LOGDIR           ("logdir",     "logs"              ),
        LOGLEVEL         ("loglevel",   Logger.LogLevel.INFO);

        String name;
        Object defaultValue;

        Key(String name, Object defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
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
        String command = cmdArgs.getParamOrDefault(0, "");
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
        // Key.SAVE_CONFIG can be specified only by args
        if (cmdArgs.hasOption(Key.SAVE_CONFIG.name)) {
            String newConfigFilePath = cmdArgs.getOption(Key.SAVE_CONFIG.name);
            if (newConfigFilePath.equals("")) {
                newConfigFilePath = DEFAULT_CONFIG_DIR + FileUtils.SEPALATOR
                        + DateUtils.getCurrentDateTimeString(DEFAULT_NEW_CONFIG_FILENAME_FORMAT) + CONFIG_FILE_EXT;
            } else if (!newConfigFilePath.endsWith(CONFIG_FILE_EXT)) {
                newConfigFilePath += CONFIG_FILE_EXT;
            }
            if (!FileUtils.isDirWritable(newConfigFilePath)) {
                System.err.println("new config file " + newConfigFilePath + " is not writable.");
                return App.Mode.NONE; // this stop reading args anymore.
            }
            putString(Key.SAVE_CONFIG, newConfigFilePath);
        }
        properties.putAll(cmdArgs.getOptions()); // override values by command line args
        putString  (Key.INPUT,            properties);
        putString  (Key.MODEL_INPUT,      properties);
        putString  (Key.TRAINING_FILE,    properties);
        putString  (Key.DEVELOPMENT_FILE, properties);
        putString  (Key.TEST_FILE,        properties);
        putString  (Key.MODEL_OUTPUT,     properties);
        putInt     (Key.ITERATION,        properties);
        // putInt     (Key.N_THREADS,        properties);
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
        boolean isset;
        Object value = instance.values.get(key);
        if (value == null) {
            isset = false;
        } else if (value instanceof String) {
            isset = !((String) value).equals("");
        } else {
            isset = true;
        }
        return isset;
    }

    static String getDump() {
        checkInitialized();
        StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<Key, Object> e : instance.values.entrySet()) {
            joiner.add(e.getKey().name + "=" + e.getValue());
        }
        return "{" + joiner.toString() + "}";
    }

    static void save() throws IOException {
        save(getString(Key.SAVE_CONFIG));
    }

    static void save(String filepath) throws IOException {
        checkInitialized();
        StringJoiner joiner = new StringJoiner(StringUtils.NEW_LINE);
        // @TODO: 7/23/16 insert header comment
        joiner.add("# created at " + DateUtils.getCurrentDateTimeString("yyyy-MM-dd HH:mm:ss"));
        if (isSet(Key.CONFIG_FILE)) {
            joiner.add("# original config file: " + getString(Key.CONFIG_FILE));
        }
        for (Map.Entry<Key, Object> e : instance.values.entrySet()) {
            Key key = e.getKey();
            if (key == Key.CONFIG_FILE || key == Key.SAVE_CONFIG) {
                continue;
            }
            joiner.add(key.name + "=" + e.getValue());
        }
        if (!filepath.endsWith(CONFIG_FILE_EXT)) {
            filepath += CONFIG_FILE_EXT;
        }
        FileWriter fw = new FileWriter(filepath, false); // override if exists
        fw.write(joiner.toString());
        fw.close();
    }

    static String getProductName() {
        return Main.PRODUCT_NAME;
    }

    static String getVersion() {
        return Main.VERSION;
    }

    static String getAuthor() {
        return Main.AUTHOR;
    }

    static String[] getUsage() {
        String[] lines = {
                "Usage:",
                "  java -jar build/teraparser.jar COMMAND [OPTIONS]",
                "",
                "Example:",
                "  java -jar build/teraparser.jar help",
                "  java -jar build/teraparser.jar train --trainfile <file> --devfile <file> [OPTIONS]",
                "  java -jar build/teraparser.jar parse --input <file> --modelin <file> [OPTIONS]",
                "",
                "train options:",
                "      --trainfile <file>     [required] Conll file to train",
                "      --devfile <file>       [required] Conll file used as development set",
                "      --testfile <file>      Conll file used for final testing (optional)",
                "      --iteration            Training iteration (default: 20)",
                "      --locally              Train greedily, otherwise train globally (structured learing)",
                "      --beamwidth <num>      Train globally using beam-search with specified beam-width (default: 16)",
                "      --early                Update weight with \"early-update\" method, otherwise use \"max-violation\"",
                "      --modelout <file>      Output learned parameters to the specified file",
                "",
                "parse options:",
                "      --input <file>         [required] Target conll file to parse",
                "      --modelin <file>       [required] Model file for parsing, which contains learing parameters",
                "      --locally              Parse greedily, otherwise parse globally (structured parsing)",
                "      --beamwidth <num>      Parse globally using beam-search with specified beam-width (default: 16)",
                "",
                "common options:",
                "      --config <file>        Specify options using config file",
                "      --saveconfig <file>    Save current options to the file",
                "      --verbose false        Turn off displaying messages to stdin and stderr",
                "      --logdir <dir>         Log output directory (default: logs)",
                "      --loglevel (info|off)  Log level (default: info)",
                "",
        };
        return lines;
    }
}
