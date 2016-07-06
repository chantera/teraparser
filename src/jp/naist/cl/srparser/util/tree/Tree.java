package jp.naist.cl.srparser.util.tree;

/**
 * jp.naist.cl.srparser.util.tree
 *
 * @author Hiroki Teranishi
 */
public class Tree {
    protected Component root;

    public Tree(Component root) {
        setRoot(root);
    }

    public void setRoot(Component component) {
        this.root = component;
    }

    public Component getRoot() {
        return root;
    }

    public void pushDown(Node node) {
        node.addChild(getRoot());
        setRoot(node);
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
