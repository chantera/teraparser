package jp.naist.cl.srparser.util.tree;

/**
 * jp.naist.cl.srparser.util.tree
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
