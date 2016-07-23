package jp.naist.cl.srparser.app;

import jp.naist.cl.srparser.io.ConllReader;
import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.parser.LocallyLearningTrainer;
import jp.naist.cl.srparser.parser.StructuredLearningTrainer;
import jp.naist.cl.srparser.parser.Trainer;
import jp.naist.cl.srparser.transition.Oracle;
import jp.naist.cl.srparser.util.DateUtils;
import jp.naist.cl.srparser.util.SystemUtils;

import java.io.File;

/**
 * jp.naist.cl.srparser.app
 *
 * @author Hiroki Teranishi
 */
public final class App {
    private static App instance = null;
    private Mode mode;
    private boolean initialized = false;

    private App() {}

    public static void execute(String[] args) {
        try {
            Config.initialize(args);
            execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void execute() {
        App app = getInstance();
        try {
            app.initialize(Config.getMode());
            switch (app.mode) {
                case PARSE:
                    app.parse();
                    break;
                case TRAIN:
                    app.train();
                    break;
                case NONE:
                    break;
                case HELP:
                default:
                    app.help();
                    break;
            }
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            app.finalize();
        }
    }

    enum Mode {
        HELP("help"),
        PARSE("parse"),
        TRAIN("train"),
        NONE("");

        final String label;

        Mode(String label) {
            this.label = label;
        }
    }

    private static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    private void initialize(Mode mode) throws Exception {
        if (initialized) {
            throw new Exception("app.initilize() can be called only once.");
        }
        if (!mode.equals(Mode.HELP) && !mode.equals(Mode.NONE)) {
            new Logger.Builder()
                    .setOutputDir(Config.getString(Config.Key.LOGDIR))
                    .setLogLevel((Logger.LogLevel) Config.getObject(Config.Key.LOGLEVEL))
                    .setVerbose(Config.getBoolean(Config.Key.VERBOSE))
                    .build();
            Logger.info("[OS INFO] " + SystemUtils.getOSInfo());
            Logger.info("[settings] " + Config.getDump());
        }
        this.mode = mode;
        this.initialized = true;
    }

    private void help() {
        Config.showHelp();
    }

    private void parse() {
        /*
        try {
            Sentence[] sentences = (new ConllReader(Config.getString(Config.Key.TRAINING_FILE))).read();
            float[][] weights = new float[Action.SIZE][Feature.SIZE];
            Parser parser = new Parser(new Perceptron(weights));
            for (Sentence sentence : sentences) {
                Logger.trace(sentence.toString());
                parser.parse(sentence);
                // Logger.trace(new DepTree(sentence));
                // Logger.trace(new DepTree(parsed));
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        */
    }

    private void train() {
        try {
            // validate args
            boolean valid = (
                validateFile(Config.Key.TRAINING_FILE,     true) &&
                validateFile(Config.Key.DEVELOPMENT_FILE,  true) &&
                validateFile(Config.Key.TEST_FILE,        false)
            );
            if (!valid) {
                return;
            }
            if (Config.isSet(Config.Key.SAVE_CONFIG)) {
                String newConfigFile = Config.getString(Config.Key.SAVE_CONFIG);
                Logger.info("saving new config file to %s ...", newConfigFile);
                Config.save(newConfigFile);
            }

            Logger.info("---- SETTING UP ----");
            int iteration    = Config.getInt(Config.Key.ITERATION);
            Trainer trainer  = loadTrainer(Config.Key.TRAINING_FILE);
            Trainer tester   = loadTrainer(Config.Key.DEVELOPMENT_FILE);
            int testPeriod   = 5;
            int trainingSize = trainer.getTrainingSize();

            Logger.info("---- TRAINING START  ----");
            for (int i = 1; i <= iteration; i++) {
                Logger.info("----8<----8<----8<---- ITERATION: %d / %d ----8<----8<----<<", i, iteration);
                long start = DateUtils.getTimeInMillis();
                trainer.train();
                long elapsedTime = DateUtils.getTimeInMillis() - start;
                Logger.info("---- ITERATION %d METRICS ----", i);
                Logger.info("Sentence Size:\t\t%d", trainingSize);
                Logger.info("Execution Time:\t\t%1.4f seconds", (double) elapsedTime / 1000);
                Logger.info("Per Sentence:\t\t%f milliseconds", (double) elapsedTime / trainingSize);
                Logger.info("Memory Usage:\t\t%.2fM of %.2fM", SystemUtils.getUsedMemoryMB(), SystemUtils.getTotalMemoryMB());
                trainer.test((gold, pred) -> {
                    Logger.info("Training UAS:\t\t%1.6f", Evaluator.calcUAS(gold, pred));
                });
                if (i % testPeriod == 0) {
                    tester.setWeights(trainer.getWeights());
                    tester.test((gold, pred) -> {
                        Logger.info("Development UAS:\t%1.6f", Evaluator.calcUAS(gold, pred));
                    });
                }
                Logger.info("//----------------------------");
            }
            Logger.info("---- TRAINING FINISHED ----");

            if (Config.isSet(Config.Key.TEST_FILE)) {
                Logger.info("---- TEST STARTED ----");
                tester = loadTrainer(Config.Key.TEST_FILE);
                tester.setWeights(trainer.getWeights());
                tester.test((gold, pred) -> {
                    Logger.info("Test UAS:\t%1.6f", Evaluator.calcUAS(gold, pred));
                });
                Logger.info("---- TEST FINISHED ----");
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    private boolean validateFile(Config.Key key, boolean required) {
        boolean valid = true;
        if (Config.isSet(key)) {
            String filepath = Config.getString(key);
            File file = new File(Config.getString(key));
            if (!file.exists()) {
                valid = false;
                Logger.error("%s=%s Not Found.", key.name, filepath);
            } else if(!file.canRead()) {
                valid = false;
                Logger.error("%s=%s is not readable.", key.name, filepath);
            }
        } else if (required) {
            valid = false;
            Logger.error("argument %s is required.", key);
        }
        return valid;
    }

    private Trainer loadTrainer(Config.Key key) throws Exception {
        String filepath;
        String label;
        switch (key) {
            case TRAINING_FILE:
                filepath = Config.getString(key);
                label = "TRAINING";
                break;
            case DEVELOPMENT_FILE:
                filepath = Config.getString(key);
                label = "DEVELOPMENT";
                break;
            case TEST_FILE:
                filepath = Config.getString(key);
                label = "DEVELOPMENT";
                break;
            default:
                throw new Exception("Invalide File Key");
        }
        Logger.info("Reading %s Samples from %s ...", label, filepath);
        Sentence[] sentences = new ConllReader(filepath).read();
        Logger.info("%s Sample size: %d", label, sentences.length);
        Logger.info("Initializeing Trainer ...");

        Trainer trainer;
        Oracle oracle = new Oracle(Oracle.Algorithm.STATIC);
        if (Config.getBoolean(Config.Key.TRAIN_LOCALLY)) {
            trainer = new LocallyLearningTrainer(sentences, oracle);
        } else {
            int beamWidth = Config.getInt(Config.Key.BEAM_WIDTH);
            boolean earlyUpate = Config.getBoolean(Config.Key.EARLY_UPDATE);
            trainer = new StructuredLearningTrainer(sentences, oracle, beamWidth, earlyUpate);
        }
        return trainer;
    }

    @Override
    protected void finalize() {
        try {
            Logger.terminate();
            instance = null;
        } finally {
            try {
                super.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
