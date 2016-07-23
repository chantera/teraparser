package jp.naist.cl.srparser.util;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * jp.naist.cl.srparser.util
 *
 * @author Hiroki Teranishi
 */
public class TestUtils {
    private TestUtils() {
        throw new AssertionError();
    }

    public static Sentence getSampleSentence() {
        String[] lines = {
                "1	But	_	CC	CC	_	33	VMOD	_	_",
                "2	while	_	IN	IN	_	33	VMOD	_	_",
                "3	the	_	DT	DT	_	7	NMOD	_	_",
                "4	New	_	NN	NNP	_	7	NMOD	_	_",
                "5	York	_	NN	NNP	_	7	NMOD	_	_",
                "6	Stock	_	NN	NNP	_	7	NMOD	_	_",
                "7	Exchange	_	NN	NNP	_	8	SUB	_	_",
                "8	did	_	VB	VBD	_	2	SBAR	_	_",
                "9	n't	_	RB	RB	_	8	VMOD	_	_",
                "10	fall	_	VB	VB	_	8	VC	_	_",
                "11	apart	_	RB	RB	_	10	VMOD	_	_",
                "12	Friday	_	NN	NNP	_	10	VMOD	_	_",
                "13	as	_	IN	IN	_	10	VMOD	_	_",
                "14	the	_	DT	DT	_	18	NMOD	_	_",
                "15	Dow	_	NN	NNP	_	18	NMOD	_	_",
                "16	Jones	_	NN	NNP	_	18	NMOD	_	_",
                "17	Industrial	_	NN	NNP	_	18	NMOD	_	_",
                "18	Average	_	NN	NNP	_	19	SUB	_	_",
                "19	plunged	_	VB	VBD	_	13	SBAR	_	_",
                "20	190.58	_	CD	CD	_	21	NMOD	_	_",
                "21	points	_	NN	NNS	_	19	VMOD	_	_",
                "22	--	_	:	:	_	23	P	_	_",
                "23	most	_	JJ	JJS	_	21	NMOD	_	_",
                "24	of	_	IN	IN	_	23	NMOD	_	_",
                "25	it	_	PR	PRP	_	24	PMOD	_	_",
                "26	in	_	IN	IN	_	23	NMOD	_	_",
                "27	the	_	DT	DT	_	29	NMOD	_	_",
                "28	final	_	JJ	JJ	_	29	NMOD	_	_",
                "29	hour	_	NN	NN	_	26	PMOD	_	_",
                "30	--	_	:	:	_	23	P	_	_",
                "31	it	_	PR	PRP	_	33	SUB	_	_",
                "32	barely	_	RB	RB	_	33	VMOD	_	_",
                "33	managed	_	VB	VBD	_	0	ROOT	_	_",
                "34	to	_	TO	TO	_	35	VMOD	_	_",
                "35	stay	_	VB	VB	_	33	VMOD	_	_",
                "36	this	_	DT	DT	_	37	NMOD	_	_",
                "37	side	_	NN	NN	_	35	VMOD	_	_",
                "38	of	_	IN	IN	_	37	NMOD	_	_",
                "39	chaos	_	NN	NN	_	38	PMOD	_	_",
                "40	.	_	.	.	_	33	P	_	_",
        };
        List<Token> tokens = Arrays.stream(lines).map(line -> new Token(line.split("\\t"))).collect(Collectors.toList());
        tokens.add(0, Token.createRoot());
        return new Sentence(0, tokens.toArray(new Token[tokens.size()]));
    }
}
