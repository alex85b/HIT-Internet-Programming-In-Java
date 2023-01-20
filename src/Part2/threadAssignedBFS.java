package Part2;


import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * this class is design to perform a bfs-like traverse, concurrently.
 * it implements TraverseLogic and as such will provide a Traverse method.
 */
public class threadAssignedBFS implements TraverseLogic<IndexAsNode, bfsCoordinator> {

    private static ThreadLocal<HashMap<UUID, bfsVertex>> bfsUtility = ThreadLocal.withInitial(() -> {
        HashMap<UUID, bfsVertex> inner = new HashMap<>();
        return inner;
    });
    private static ThreadLocal<LinkedList<IndexAsNode>> Q = ThreadLocal.withInitial(() -> {
       LinkedList<IndexAsNode> inner = new LinkedList<>();
        return inner;
    });
    private static ThreadLocal<HashMap<UUID, HashSet<IndexAsNode>>> localAdjacency = ThreadLocal.withInitial(() -> {
        HashMap<UUID, HashSet<IndexAsNode>> inner = new HashMap<>();
        return inner;
    });

    private static ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

    /**
     * set up method prior to the bfs-like traverse,
     * this will prepare the Queue, bfs-vertices-information {parent, distance, status}.
     * @param S start index.
     * @param bfsCoordinator utility object that used to share results and coordinate vertices discovery,
     *                       between different threads,
     *                       that run the concurrent-bfs-algorithm.
     * @return indication of a success of the set up.
     */
    private boolean setUp(IndexAsNode S, bfsCoordinator bfsCoordinator) {
        try {
            bfsUtility.get().clear(); // reset utility.
            this.Q.get().clear(); // reset queue.
            if(!bfsCoordinator.verticesRemained()) return false; // no vertices left to discover.
            HashSet<IndexAsNode> setUp = bfsCoordinator.getRemainingVertices();
            for (IndexAsNode ind : setUp) {
                bfsUtility.get().put(ind.getId(), new bfsVertex());
            }
            bfsUtility.get().get(S.getId()).Status = VertexStatus.visited;
            return true;
        } catch (Exception e) {
            System.err.println("threadAssignedBFS::threadAssignedBFS has failed. on index={"+S.unWrap()+"}");
            System.err.println(e.toString());
            return false;
        }
    }

    /**
     * this method will perform a BFS(like) traverse of an Objet that implements Traversable interface.
     * @param startNode Object that extends INode, Traverse will start from this Node.
     * @param bfsCoordinator an Object that Extends ITraverseUtility which provides tracking of remaining indices,
     *                       and vertices that have been discovered by other thread that are executing concurrently.
     * @param adjacency an adjacency list of the graph vertices.
     * @return an HashSet of all the Vertices in the discovered graph component.
     */
    @Override
    public HashSet<IndexAsNode> traverse(@NotNull IndexAsNode startNode,
                                         @NotNull bfsCoordinator bfsCoordinator,
                                         @NotNull HashMap<UUID, HashSet<IndexAsNode>> adjacency) {
        IndexAsNode S = startNode;
        final String myName = Thread.currentThread().getName();
        String sharedPoolProbe;

        // if incoming vertices list is empty --> nothing left to discover, get out.
        ///////////////////////////////////////////////////////////////////////////
        if(!bfsCoordinator.verticesRemained()) return null;
        localAdjacency.set((HashMap<UUID, HashSet<IndexAsNode>>) adjacency.clone());

        // vertices pool wil be shared for all threadedBfs instances.
        ////////////////////////////////////////////////////////////
        //System.out.println(Thread.currentThread().getName()+" Starts with "+S.unWrap());
        HashSet<IndexAsNode> connectivity = new HashSet<>();// <--- local discovery results.
        if (setUp(S,bfsCoordinator)) {// <---- if BFS utility set up has not failed: for each shared vertex, created BFS status.
            try {
                Q.get().add(S);// <------------- start up the queue.
                while(!Q.get().isEmpty()) {
                    IndexAsNode u = Q.get().remove();
                    for(IndexAsNode n : adjacency.get(u.getId())) {// <----- for any neighbour of u do:

                        if(!bfsUtility.get().containsKey(n.getId()))
                            continue;

                        if(bfsUtility.get().get(n.getId()).Status != VertexStatus.notVisited)
                            continue;

                        sharedPoolProbe = bfsCoordinator.checkVertexOwnership(u,myName);
                        if(sharedPoolProbe.compareTo(myName) != 0){ // other instance of bfs found my graph component.
                            bfsCoordinator.setSplitComponentFlag(true);
                            continue;
                        }else{
                            bfsCoordinator.addSharedVertex(n,Thread.currentThread().getName());
                            bfsUtility.get().get(n.getId()).Status = VertexStatus.visited;
                            bfsUtility.get().get(n.getId()).Parent = u;
                            Q.get().add(n);
                        }
                    }
                    bfsUtility.get().get(u.getId()).Status = VertexStatus.done;// <--- u has finished its role.
                    bfsCoordinator.removeVertexFromRemaining(u,Thread.currentThread().getName());
                    //System.out.println(Thread.currentThread().getName()+" adds "+S.unWrap()+" to Local-Pool");
                    sharedPoolProbe = bfsCoordinator.checkVertexOwnership(u,myName);
                    if(sharedPoolProbe.compareTo(myName) == 0){
                        connectivity.add(u);
                        bfsCoordinator.addSharedVertex(u,Thread.currentThread().getName());
                    }
                    else bfsCoordinator.setSplitComponentFlag(true);
                }
                return connectivity;
            }catch (Exception e) {
                System.err.println("threaded BFS traverse has failed");
                System.err.println(e.toString());
                return null;
            }
        }
        return null;
    }

    /**
     * Utility Class that provides BFS discovery of a vertex.
     */
    private class bfsVertex {
        private IndexAsNode Parent = null;
        private VertexStatus Status = VertexStatus.notVisited;
        private int Distance = -1;

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
}
