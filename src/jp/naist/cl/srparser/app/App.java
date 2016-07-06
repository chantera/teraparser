package jp.naist.cl.srparser.app;

import jp.naist.cl.srparser.io.ConllReader;
import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.DepTree;
import jp.naist.cl.srparser.parser.Parser;

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
            app.run();
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
        Sentence[] sentences = (new ConllReader()).read(Config.getString(Config.Key.TRAINING_FILE));
        Parser parser = new Parser();
        for (Sentence sentence : sentences) {
            Logger.trace(sentence.toString());
            Sentence parsed = parser.parse(sentence);
            Logger.trace(new DepTree(sentence));
            Logger.trace(new DepTree(parsed));
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
