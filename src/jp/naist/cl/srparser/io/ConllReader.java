package jp.naist.cl.srparser.io;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * jp.naist.cl.srparser.io
 *
 * @author Hiroki Teranishi
 */
public class ConllReader extends Reader {
    private static final String DELIMITER = "\t";

    public ConllReader(String filepath) throws FileNotFoundException {
        super(filepath);
    }

    @Override
    public Sentence[] read() throws Exception {
        ArrayList<Sentence> sentences = new ArrayList<>();
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(Token.createRoot());

        String line;
        int sentenceCount = 0;
        while ((line = fileReader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0 ) {
                if (tokens.size() > 1) {
                    sentences.add(new Sentence(++sentenceCount, tokens.toArray(new Token[tokens.size()])));
                    tokens = new ArrayList<>();
                    tokens.add(Token.createRoot());
                }
            } else {
                tokens.add(new Token(line.split(DELIMITER)));
            }
        }
        if (tokens.size() > 1) {
            sentences.add(new Sentence(++sentenceCount, tokens.toArray(new Token[tokens.size()])));
        }
        return sentences.toArray(new Sentence[sentences.size()]);
    }
}
