package Part2;

import java.util.List;

/**
 * this interface will group all the objects that intended to be traversed,
 * group members will have to provide a path discovery mechanism.
 */
public interface Traversable  {

    /**
     * this method will provide a path discovery solution.
     * @param Start the node from which a path should start.
     * @param End the node that will be the path end.
     * @param method  method to traverse the graph.
     * @param <T> a type that extend the INode interface and will perform as a node in a graph.
     * @return a list of shortest paths between Start to End.
     */
    public <T extends INode> List<List<Index>> getPath (T Start, T End, TraverseLogic method);
}