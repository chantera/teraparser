package jp.naist.cl.srparser.model;

import jp.naist.cl.srparser.util.StringUtils;
import jp.naist.cl.srparser.util.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class DepTreeTest {
    private Sentence sentence;

    @Before
    public void setUp() throws Exception {
        sentence = TestUtils.getSampleSentence();
        System.out.println(sentence);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test() {
        DepTree tree = new DepTree(sentence);
        System.out.println(StringUtils.join(tree.getTreeExpr(), '\n'));
        assertTrue(true);
    }
}