package jp.naist.cl.srparser.io;

import jp.naist.cl.srparser.model.Sentence;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * jp.naist.cl.srparser.io
 *
 * @author Hiroki Teranishi
 */
public abstract class Reader implements LineParser {
    protected boolean isBOF;
    protected boolean isEOF;

    public Sentence[] read(String filepath) {
        ArrayList<Sentence> sentences = new ArrayList<>();
        isBOF = false;
        isEOF = false;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filepath), StandardCharsets.UTF_8)) {
            String line = br.readLine();
            isBOF = (line != null);
            while (line != null) {
                String nextLine = br.readLine();
                if (nextLine == null) {
                    isEOF = true;
                }
                Sentence sentence = parse(line);
                if (sentence != null) {
                    sentences.add(sentence);
                }
                line = nextLine;
                if (isBOF) {
                    isBOF = false;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sentences.toArray(new Sentence[sentences.size()]);
    }
}
