package jp.naist.cl.srparser.util;

import java.io.PrintStream;

/**
 * jp.naist.cl.srparser.util
 *
 * @author Hiroki Teranishi
 */
public class ProgressBar {
    private static final String FORMAT = "\r%3d%% [%s]";
    private static final String DEFAULT_BARSTR = "#";
    private static final int DEFAULT_WIDTH = 50;
    private String barStr;
    private final int width;
    private final PrintStream stream;
    private final StringBuilder sb;

    public ProgressBar(PrintStream stream) {
        this.barStr = DEFAULT_BARSTR;
        this.width = DEFAULT_WIDTH;
        this.stream = stream;
        this.sb = new StringBuilder(width + 10);
    }

    public void setProgress(int progress, int total) {
        setProgress((double) progress / total);
    }

    public void setProgress(double progress) {
        int p = (int) (progress * width);
        int i = 0;
        for (; i < p; i++) {
            sb.append(barStr);
        }
        for (; i < width; i++) {
            sb.append(" ");
        }
        stream.printf(FORMAT, (int) (progress * 100), sb.toString());
        sb.setLength(0);
        if (progress >= 1.0) {
            stream.flush();
            stream.println();
        }
    }
}
