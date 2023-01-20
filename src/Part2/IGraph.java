package Part2;

import java.util.HashSet;
import java.util.List;

/**
 * this in an interface that meant to group Objects that can be considered as a graph,
 * this will require group members to implement a graph-connectivity-component discovery
 * @param <T> any Object that implements Inode.
 */
public interface IGraph<T extends INode> {
    /**
     * this should perform a graph-connectivity-component discovery,
     * using an Object that implements TraverseLogic interface.
     * @param method a method to traverse a graph.
     * @return all the connectivity components of a graph.
     */
    public List<HashSet<T>> getConnectivity(TraverseLogic method);
}
