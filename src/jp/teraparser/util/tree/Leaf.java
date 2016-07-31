package jp.teraparser.util.tree;

/**
 * jp.teraparser.util.tree
 *
 * @author Hiroki Teranishi
 */
public class Leaf<T> implements Component<T> {
    protected T data;

    public Leaf(T data) {
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

    @Override
    public String toString() {
        return data.toString();
    }
}
