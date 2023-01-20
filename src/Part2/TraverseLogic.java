package Part2;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * this interface is intended to group all the objects that provide a Traverse solutions for IGraph.
 * @param <T> type that extends the INode interface and represents an Node of IGraph.
 * @param <T1> a Traverse utility that needed to coordinate the Traverse, between the algorithm,
 *            and the caller class.
 *            Coordination may include:
 *            not-discovered vertices for discovery of graph connectivity components,
 *            and shared results for concurrent runs.
 */
public interface TraverseLogic<T extends INode, T1 extends ITraverseUtility<T>> {

    /**
     * a single traverse of a group of vertices.
     * @param startNode start point of the traverse.
     * @param TraverseUtility the coordinator object that contains vertices to traverse
     *                        and tracks non traversed vertices and more.
     * @param adjacency the adjacency list for each vertex of the graph.
     * @return a collection that represents discovered graph component.
     */
    public Collection<T> traverse(@NotNull T startNode,
                                  @NotNull T1 TraverseUtility,
                                  @NotNull HashMap<UUID, HashSet<T>> adjacency);

}


