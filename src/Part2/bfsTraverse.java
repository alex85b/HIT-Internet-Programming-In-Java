package Part2;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * this class will implement a bfs traverse as a single, stand-alone discovery.
 * suited for a non-concurrent run, will not be suited for a concurrent run upon a shared vertices pool.
 * this class implements the TraverseLogic interface upon the IndexAsNode which implement the INode interface,
 * and thus declares itself to have the "traverse" method.
 */

public class bfsTraverse implements TraverseLogic<IndexAsNode, bfsCoordinator> {

    private HashMap<UUID, bfsVertex> bfsUtility;
    private bfsCoordinator TraverseUtility;
    private LinkedList<IndexAsNode> Q;
    private HashMap<UUID, HashSet<IndexAsNode>> adjacency;
    private IndexAsNode S;

    /**
     * a private method that sets up the BFS utility according to the vertices that are left to discover.
     * the BFS utility will link each undiscovered vertex to
     * the bfsVertex={Vertex Parent, Vertex Distance, Vertex Status={Visited/Not Visited/Done}}.
     * @return a boolean that represent an answer to the question "has the set up process succeed ?".
     */
    private boolean setUp() {
        try {
            this.bfsUtility = new HashMap<>();
            this.Q = new LinkedList<>();
            if (!TraverseUtility.verticesRemained()) return false;
            for (IndexAsNode ind : TraverseUtility.getRemainingVertices()) {
                bfsUtility.put(ind.getId(), new bfsVertex());
            }
        } catch (Exception e) {
            System.err.println("Set up for BFS has failed");
            return false;
        }
        try {
            bfsUtility.get(S.getId()).Distance = 0;
            bfsUtility.get(S.getId()).Status = VertexStatus.visited;
        }catch (Exception e) {
            System.err.println("S is not a member of the graph");
            return false;
        }
        return true;
    }

    /**
     * this method will perform a BFS(like) traverse of an Objet that implements Traversable interface.
     * @param startNode Object that extends INode, Traverse will start from this Node.
     * @param TraverseUtility an Object that Extends ITraverseUtility which provides tracking of remaining indices.
     * @param adjacency an adjacency list of the graph vertices.
     * @return an HashSet of all the Vertices in the discovered graph component.
     */
    @Override
    public HashSet<IndexAsNode> traverse(@NotNull IndexAsNode startNode,
                                         @NotNull bfsCoordinator TraverseUtility,
                                         @NotNull HashMap<UUID, HashSet<IndexAsNode>> adjacency) {
        this.S = startNode;
        this.adjacency = adjacency;
        HashSet<IndexAsNode> connectivity = new HashSet<>();
        this.TraverseUtility = TraverseUtility;
        if (setUp()) {
            try {
                Q.add(S);
                while(!Q.isEmpty()) {
                    IndexAsNode u = Q.remove();
                    for(IndexAsNode n : adjacency.get(u.getId())) {
                        if(bfsUtility.get(n.getId()).Status == VertexStatus.notVisited) {
                            bfsUtility.get(n.getId()).Status = VertexStatus.visited;
                            bfsUtility.get(n.getId()).Distance = bfsUtility.get(u.getId()).Distance +1;
                            bfsUtility.get(n.getId()).Parent = u;
                            Q.add(n);
                        }
                    }
                    Q.remove(u);
                    this.TraverseUtility.removeVertexFromRemaining(u,Thread.currentThread().getName());
                    connectivity.add(u);
                }
                return connectivity;
            }catch (Exception e) {
                System.err.println("BFS traverse has failed");
                return null;
            }
        }
        return null;
    }

    /**
     * Utility Class that provides BFS discovery of a vertex.
     */
    public class bfsVertex {
        public IndexAsNode Parent = null;
        public VertexStatus Status = VertexStatus.notVisited;
        public int Distance = -1;

        /**
         * @return a string representation of a BFS vertex.
         */
        @Override
        public String toString() {
            return "bfsVertex{" +
                    "Parent=" + (Parent == null ? "null": Parent.unWrap().toString()) +
                    ", Status=" + Status +
                    ", Distance=" + (Distance == -1 ? "infinity": Distance) +
                    '}';
        }
    }

    /**
     * returns the result of BFS discovery.
     * @return a hash map that contains for each given vertex its "bfs status" = {Parent, Distance, Status}.
     */
    public HashMap<UUID, bfsVertex> getBfsResultTable () {
        return this.bfsUtility;
    }
}
