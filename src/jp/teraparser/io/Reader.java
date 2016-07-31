package jp.teraparser.io;

import jp.teraparser.model.Sentence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * jp.teraparser.io
 *
 * @author Hiroki Teranishi
 */
abstract class Reader {
    BufferedReader fileReader;

    Reader(String filepath) throws FileNotFoundException {
        fileReader = new BufferedReader(new FileReader(filepath));
    }

    Reader(File file) throws FileNotFoundException {
        fileReader = new BufferedReader(new FileReader(file));
    }

    public abstract Sentence[] read() throws Exception;
}
