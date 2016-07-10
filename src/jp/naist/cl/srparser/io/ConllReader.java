package jp.naist.cl.srparser.io;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * jp.naist.cl.srparser.io
 *
 * @author Hiroki Teranishi
 */
public class ConllReader extends Reader {
    private static final String DELIMITER = "\t";
    private static final String SEPARATOR = "";
    private boolean readingSentence;
    private List<Token> readingTokens;
    private int sentenceCount = 0;

    @Override
    public Sentence parse(String line) {
        if (isBOF) {
            readingSentence = false;
            readingTokens = new ArrayList<>();
            readingTokens.add(Token.createRoot());
        }
        line = line.trim();
        if (readingSentence && (line.equals(SEPARATOR) || isEOF)) {
            Sentence sentence = null;
            if (readingTokens.size() > 0) {
                sentenceCount++;
                sentence = new Sentence(sentenceCount, readingTokens.toArray(new Token[readingTokens.size()]));
                readingTokens = new ArrayList<>();
                readingTokens.add(Token.createRoot());
            }
            readingSentence = false;
            return sentence;
        } else {
            readingSentence = true;
        }
        String[] attributes = line.split(DELIMITER);
        readingTokens.add(new Token(attributes));
        return null;
    }
}
