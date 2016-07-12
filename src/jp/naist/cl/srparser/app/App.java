package jp.naist.cl.srparser.app;

import jp.naist.cl.srparser.io.ConllReader;
import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.DepTree;
import jp.naist.cl.srparser.parser.Parser;
import jp.naist.cl.srparser.parser.Trainer;
import jp.naist.cl.srparser.parser.Trainer.Training;

/**
 * jp.naist.cl.srparser.app
 *
 * @author Hiroki Teranishi
 */
public final class App {
    private static App instance = null;

    private App() {
        initialize();
    }

    public static void execute(String[] args) {
        App app = getInstance();
        try {
            // app.run();
            app.train();
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            app.finalize();
        }
    }

    private static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    private void initialize() {
        // Config.initialize(new Config.CmdLineArgs(args));
        Config.initialize(new Config.DebugArgs());
    }

    private void run() {
        try {
            Sentence[] sentences = (new ConllReader()).read(Config.getString(Config.Key.TRAINING_FILE));
            Parser parser = new Parser();
            for (Sentence sentence : sentences) {
                Logger.trace(sentence.toString());
                Sentence parsed = parser.parse(sentence);
                Logger.trace(new DepTree(sentence));
                Logger.trace(new DepTree(parsed));
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    private void train() {
        try {
            Sentence[] sentences = (new ConllReader()).read(Config.getString(Config.Key.TRAINING_FILE));
            Trainer trainer = new Trainer(sentences);
            Training training = trainer.getIterator(Config.getInt(Config.Key.ITERATION));
            while (training.hasNext()) {
                Logger.info("Iteration: %d", training.getCurrentIteration());
                training.exec();
                training = training.next();
                double uas = Evaluator.calcUAS(training.getGoldArcSets(), training.getArcSets());
                Logger.info("UAS: %1.6f", uas);
                Logger.info("================");
            }
        } catch (Exception e) {
            Logger.error(e);
        }
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
