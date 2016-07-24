package jp.naist.cl.srparser.model;

import jp.naist.cl.srparser.util.tree.Component;
import jp.naist.cl.srparser.util.tree.Node;
import jp.naist.cl.srparser.util.tree.Tree;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class DepTree extends Tree<Token> {

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

    /*
    @Override
    public String[] getTreeExpr(String indent) {
        int componentCount = ((Node) root).countDescendent() + 1;
        int maxWidth = componentCount * 2;
        int height = getHeight();
        String[][] table = new String[height][maxWidth];
        int y = 0;
        int x = componentCount - 1;
        // table[y][x] = root.getData().toString();
        putComponentOnTable(table, root, x, y);
    }

    private void putChildrenOnTable(String[][] table, Component component, int x, int y) {
        if (table[y][x] == null) {
            table[y][x] = root.getData().toString();
        } else {

        }


    }
    */
}
