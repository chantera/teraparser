package jp.naist.cl.srparser.util.tree;

/**
 * jp.naist.cl.srparser.util.tree
 *
 * @author Hiroki Teranishi
 */
public interface Component<T> {
    public T getData();
    public void setData(T data);
    public String toString();
}
