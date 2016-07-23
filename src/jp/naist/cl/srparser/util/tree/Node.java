package jp.naist.cl.srparser.util.tree;

import java.util.ArrayList;

/**
 * jp.naist.cl.srparser.util.tree
 *
 * @author Hiroki Teranishi
 */
public class Node<T> implements Component<T> {
    protected T data;
    protected ArrayList<Component<T>> children = new ArrayList<>();

    public Node(T data) {
        this.data = data;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public void setData(T data) {
        this.data = data;
    }

    public void addChild(Component<T> component) {
        children.add(component);
    }

    public Component<T> getChild(int index) {
        return children.get(index);
    }

    public Component<T> getLastChild() {
        return children.get(children.size() - 1);
    }

    public Component<T> removeLastChild() {
        return children.remove(children.size() - 1);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(" + data);
        for (Component child : children) {
            builder.append(" " + child );
        }
        builder.append(")");
        return builder.toString();
    }
}
