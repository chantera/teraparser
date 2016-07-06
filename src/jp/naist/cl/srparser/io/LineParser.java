package jp.naist.cl.srparser.io;

import jp.naist.cl.srparser.model.Sentence;

/**
 * jp.naist.cl.srparser.io
 *
 * @author Hiroki Teranishi
 */
public interface LineParser {
    public Sentence parse(String line);
}
