package jp.teraparser.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * jp.teraparser.util
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
