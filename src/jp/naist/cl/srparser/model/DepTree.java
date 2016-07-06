package jp.naist.cl.srparser.model;

import jp.naist.cl.srparser.util.tree.Node;
import jp.naist.cl.srparser.util.tree.Tree;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class DepTree extends Tree {

    public DepTree(Sentence sentence) {
        super(buildNodes(sentence.tokens));
    }

    private static Node<Token> buildNodes(Token[] tokens) {
        @SuppressWarnings("unchecked")
        Node<Token>[] nodes = new Node[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            nodes[i] = new Node<>(tokens[i]);
        }
        for (int i = 1; i < nodes.length; i++) {
            Token dependent = nodes[i].getData();
            for (int j = 0; j < tokens.length; j++) {
                if (tokens[j].id == dependent.head) {
                    nodes[j].addChild(nodes[i]);
                    break;
                }
            }
        }
        return nodes[0];
    }
}
