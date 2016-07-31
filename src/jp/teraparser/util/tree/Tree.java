package jp.teraparser.util.tree;

import jp.teraparser.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * jp.teraparser.util.tree
 *
 * @author Hiroki Teranishi
 */
public class Tree<T> {
    protected Component<T> root;

    public Tree(Component<T> root) {
        setRoot(root);
    }

    public void setRoot(Component<T> component) {
        this.root = component;
    }

    public Component<T> getRoot() {
        return root;
    }

    public int getHeight() {
        @SuppressWarnings("unchecked")
        Function<Node<T>, Integer>[] func = new Function[1];
        func[0] = node -> node.children.stream()
            .mapToInt(child -> child instanceof Node ? 1 + func[0].apply((Node<T>) child) : 1)
            .max().orElse(0);
        return root instanceof Node ? func[0].apply((Node<T>) root) : 0;
    }

    public void pushDown(Node<T> node) {
        node.addChild(getRoot());
        setRoot(node);
    }

    public String[] getTreeExpr() {
        return getTreeExpr("\t");
    }

    public String[] getTreeExpr(String indent) {
        List<String> lines = new ArrayList<>();
        pushTreeLine(lines, root, indent, 0);
        return lines.toArray(new String[lines.size()]);
    }

    private void pushTreeLine(List<String> list, Component<T> component, String indent, int depth) {
        list.add(StringUtils.repeat(indent, depth) + component.getData());
        if (component instanceof Node) {
            for (Component<T> child : ((Node<T>) component).children ) {
                pushTreeLine(list, child, indent, depth + 1);
            }
        }
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
