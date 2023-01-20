package Part2;

import java.util.Collection;
import java.util.UUID;

/**
 * an interface that groups all the Objects which supposed to represent a Node in a graph.
 * all group members have to provide a wrapping and un wrapping method for an Type T.
 * wrapping process will generate a UUID of the wrapped object.
 * @param <T> any Type.
 */
public interface INode<T> {
    public UUID wrap(T data);
    public T unWrap();
}
