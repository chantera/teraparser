package jp.naist.cl.srparser.transition;

import jp.naist.cl.srparser.io.ConllReader;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;
import jp.naist.cl.srparser.util.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * jp.naist.cl.srparser.transition
 *
 * @author Hiroki Teranishi
 */
public class ActionTest {
    Sentence sentence;

    @Before
    public void setUp() {
        sentence = TestUtils.getSampleSentence();
        System.out.println("Sample sentence: " + sentence);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void applyTest() {
        // Initial
        State state = new State(sentence);
        System.out.println(state);

        // SHIFT
        state = Action.SHIFT.apply(state);
        System.out.println(state);
        assertTrue(Token.getAttribute(state.getStackTopToken().form).equals("But"));
        assertTrue(Token.getAttribute(state.getBufferHeadToken().form).equals("while"));

        state = Action.SHIFT.apply(state);

        // LEFT
        state = Action.LEFT.apply(state);
        System.out.println(state);
        assertTrue(Token.getAttribute(state.getStackTopToken().form).equals("But"));
        assertTrue(Token.getAttribute(state.getBufferHeadToken().form).equals("the"));
        assertTrue(state.hasArc(3, 2));

        state = Action.SHIFT.apply(state);
        state = Action.SHIFT.apply(state);

        // REDUCE
        state = Action.REDUCE.apply(state);
        System.out.println(state);
        assertTrue(Token.getAttribute(state.getStackTopToken().form).equals("the"));
        assertTrue(Token.getAttribute(state.getBufferHeadToken().form).equals("York"));

        // RIGHT
        state = Action.RIGHT.apply(state);
        System.out.println(state);
        assertTrue(Token.getAttribute(state.getStackToken(1).form).equals("the"));
        assertTrue(Token.getAttribute(state.getStackTopToken().form).equals("York"));
        assertTrue(Token.getAttribute(state.getBufferHeadToken().form).equals("Stock"));
        assertTrue(state.hasArc(3, 5));
    }

    @Test
    public void oracleTest() throws Exception {
        Sentence[] sentences = new ConllReader("/Users/hiroki/Desktop/NLP/data/penn_conll/wsj_02.conll").read();
        boolean equal = true;
        sentenceLoop:
        for (Sentence sentence : sentences) {
            System.out.println(sentence);
            State state = new Oracle(Oracle.Algorithm.STATIC).getState(sentence);
            // System.out.println(state);
            // Arrays.stream(state.getActions()).forEach(action -> System.out.println(action));
            Arc[] goldArcs = new Arc[sentence.tokens.length];
            int i = 0;
            for (Token token : sentence.tokens) {
                if (!token.isRoot()) {
                    goldArcs[++i] = new Arc(token.head, token.id);
                }
            }
            Arc[] arcs = state.arcs;
            for (i = 1; i < arcs.length; i++) {
                if (!goldArcs[i].equals(arcs[i])) {
                    equal = false;
                    break sentenceLoop;
                }
                System.out.println(goldArcs[i] + " : " + arcs[i]);
            }
        }
        assertTrue(equal);
    }

    @Test
    public void stateTest() {
        State state = new Oracle(Oracle.Algorithm.STATIC).getState(sentence);
        Token leftmost = state.getLeftmostToken(7);
        assertTrue(leftmost.id == 3);
        leftmost = state.getLeftmostToken(26);
        assertTrue(leftmost == null);
        Token rightmost = state.getRightmostToken(26);
        assertTrue(rightmost.id == 29);
        leftmost = state.getLeftmostToken(33);
        assertTrue(leftmost.id == 1);
        rightmost = state.getRightmostToken(33);
        assertTrue(rightmost.id == 40);
    }
}