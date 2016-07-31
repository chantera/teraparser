package jp.teraparser.util.tree;

/**
 * jp.teraparser.util.tree
 *
 * @author Hiroki Teranishi
 */
public interface Component<T> {
    public T getData();
    public void setData(T data);
    public String toString();
}
