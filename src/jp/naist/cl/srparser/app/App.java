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

/**
 * jp.naist.cl.srparser.app
 *
 * @author Hiroki Teranishi
 */
public final class App {
    private static App instance = null;

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
            app.initialize();
            switch (Config.getMode()) {
                case PARSE:
                    app.parse();
                    break;
                case TRAIN:
                    app.train();
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
        TRAIN("train");

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

    private void initialize() throws Exception {
        new Logger.Builder()
                .setOutputDir(Config.getString(Config.Key.LOGDIR))
                .setLogLevel((Logger.LogLevel) Config.getObject(Config.Key.LOGLEVEL))
                .setVerbose(Config.getBoolean(Config.Key.VERBOSE))
                .build();
        Logger.info("[OS INFO] " + SystemUtils.getOSInfo());
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
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    private Trainer loadTrainer(Config.Key file) throws Exception {
        String filepath;
        String label;
        switch (file) {
            case TRAINING_FILE:
                filepath = Config.getString(file);
                label = "TRAINING";
                break;
            case DEVELOPMENT_FILE:
                filepath = Config.getString(file);
                label = "DEVELOPMENT";
                break;
            default:
                throw new Exception("Invalide File Key");
        }
        Logger.info("Reading %s Samples from %s ...", label, filepath);
        Sentence[] sentences = new ConllReader(filepath).read();
        Logger.info("%s Sample size: %d", label, sentences.length);
        Logger.info("Initializeing Trainer ...");

        // Trainer trainer = new StructuredLearningTrainer(sentences, new Oracle(Oracle.Algorithm.STATIC), 8);
        Trainer trainer = new LocallyLearningTrainer(sentences, new Oracle(Oracle.Algorithm.STATIC));
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
