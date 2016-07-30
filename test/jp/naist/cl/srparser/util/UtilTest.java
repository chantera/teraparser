package jp.naist.cl.srparser.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * jp.naist.cl.srparser.util
 *
 * @author Hiroki Teranishi
 */
public class UtilTest {

    @Test
    public void progressBarTest() throws InterruptedException {
        ProgressBar progressBar = new ProgressBar(System.out);
        for (int i = 1; i <= 500; i++) {
            progressBar.setProgress(i, 500);
            Thread.sleep(100);
        }
        assertTrue(true);
    }
}
