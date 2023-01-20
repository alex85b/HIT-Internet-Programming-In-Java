package Part2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * this class is intended to coordinate between two BFS algorithms that will run concurrently,
 * (like dual wielding swords but with BFS instead of swords.)
 * this class will help algorithms to map the same graph component efficiently,
 * by providing a shared vertices pool,
 * and monitoring the vertices that are yet to be discovered at any point of time.
 * access to the shared pool will be limited to provide thread safety.
 */

public class bfsCoordinator implements ITraverseUtility<IndexAsNode>{

    private HashMap<IndexAsNode,String> sharedResults = new HashMap<>();

    private HashSet<IndexAsNode> remainingVertices = new HashSet<>();

    private boolean sameComponentFlag = false;

    private static ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

    private boolean debug = true;

    /**
     * this method turns on or off the "debug-mode" in which a log will be displayed for many class methods.
     * @param debug a boolean that represent the answer to the question "should this display logs ?"
     */
    public void setDebug (boolean debug) {
        this.debug = debug;
    }

    /**
     * this method finds and returns the thread that registered the given vertex in the shared-vertices-pool.
     * if no one has registered the vertex, the method will add given vertex to the shared-vertices-pool.
     * only one thread can access the shared-vertices-pool at any given moment.
     * @param v a vertex that was discovered by a bfs.
     * @param ThreadName the name of the thread that found given vertex, will be used as an id.
     * @return the name of the thread that registered the given vertex.
     */
    public String checkVertexOwnership (IndexAsNode v, String ThreadName) {
        try{
            LOCK.writeLock().lock();
            if (sharedResults.containsKey(v))
                return sharedResults.get(v);
            else {
                sharedResults.put(v,ThreadName);
                return ThreadName;
            }
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    /**
     * this method attempts to add a new vertex to the shared-vertices-pool,
     * if given vertex is already registered, the method will notify that attempt has failed.
     * if the vertex isn't found in the shared-vertices-pool,
     * the vertex will be registered and linked to the provided thread name.
     * @param v a vertex that was discovered by a bfs.
     * @param ThreadName the name of the thread that found given vertex, will be used as an id.
     * @return a boolean that represent the answer to the question "has the attempts succeeded ?".
     */
    public boolean addSharedVertex (IndexAsNode v, String ThreadName) {
        try{
            LOCK.writeLock().lock();
            boolean exist = sharedResults.containsKey(v);
            if(exist) return false;
            sharedResults.put(v,ThreadName);
            return true;
        } finally {
            LOCK.writeLock().unlock();
            if(debug)
                System.out.println(ThreadName+" added shared vertex "+v.unWrap());
        }
    }

    /**
     * this method attempts to updates the vertices that are yet to be discovered,
     * when a vertex is discovered by a thread that runs BFS concurrently,
     * said thread should remove the thread from the "yet to be discovered" pool.
     * @param v a vertex that was discovered by a bfs.
     * @param ThreadName the name of the thread that found given vertex, will be used as an id.
     * @return a boolean that represent the answer to the question "has the attempts succeeded ?".
     */
    public boolean removeVertexFromRemaining (IndexAsNode v, String ThreadName) {
        try {
            LOCK.writeLock().lock();
            if(remainingVertices.contains(v)){
                remainingVertices.remove(v);
                return true;
            }
            else return false;
        } finally {
            LOCK.writeLock().unlock();
            if(debug)
                System.out.println(ThreadName+" removed vertex from remaining "+v.unWrap());
        }
    }

    /**
     * this method returns the vertices that were found by all the different threads that run "thread suited bfs".
     * @return a hashset of indexes.
     */
    public HashSet<IndexAsNode> getSharedResults () {
        try {
            LOCK.readLock().lock();
            return sharedResults.keySet().stream()
                    .map(k -> new IndexAsNode(k.unWrap())).collect(Collectors.toCollection(HashSet::new));
        }finally {
            LOCK.readLock().unlock();
        }
    }

    /**
     * reset of the shared-vertices-pool.
     */
    public void clearSharedResults() {
        LOCK.writeLock().lock();
        sharedResults.clear();
        LOCK.writeLock().unlock();
    }

    /**
     * this method allows to target undiscovered graph components, after a run of bfs algorithm.
     * @return a cloned hashset of indexes.
     */
    @Override
    public HashSet<IndexAsNode> getRemainingVertices () {
        try {
            LOCK.readLock().lock();
            return (HashSet<IndexAsNode>) remainingVertices.clone();
        }finally {
            LOCK.readLock().unlock();
        }
    }

    /**
     * this method allows set the vertices that we want to target for discovery by bfs algorithm.
     * @param remaining a hashset of node-indexes.
     */
    @Override
    public void setRemainingVertices(HashSet<IndexAsNode> remaining) {
        LOCK.writeLock().lock();
        remainingVertices = (HashSet<IndexAsNode>) remaining;
        LOCK.writeLock().unlock();
        if(debug)
            System.out.println(Thread.currentThread().getName()+" new vertices pool");
    }

    /**
     * this method allows to inquire if there any vertices that were chosen for discovery,
     * and are yet to be discovered.
     * @return a boolean that represent the answer to the question "is there any vertices left to discover ?".
     */
    public boolean verticesRemained() {
        try {
            LOCK.readLock().lock();
            return (!remainingVertices.isEmpty());
        }finally {
            LOCK.readLock().unlock();
        }
    }

    /**
     * this method allows to inquire if both the bfs algorithms found one, shared component.
     * @return a boolean that represent the answer to the question:
     * "have both Threads that run bfs solution encountered the same component ?".
     */
    public boolean splitComponentFlag() {
        try {
            LOCK.readLock().lock();
            return sameComponentFlag;
        }finally {
            LOCK.readLock().unlock();
        }
    }

    /**
     * this method allows to notice that both the bfs algorithms found one, shared component during the discovery.
     * @param isSplit a boolean that represent the answer to the question:
     * "have both Threads that run bfs solution encountered the same component ?".
     */
    public void setSplitComponentFlag(boolean isSplit) {
        try {
            LOCK.writeLock().lock();
            sameComponentFlag= isSplit;
        }finally {
            LOCK.writeLock().unlock();
        }
    }

    /**
     * displays all the vertices that are targeted for discovery and are yet to be discovered.
     */
    public void displayRemaining () {
        try {
            System.out.println("Remaining Vertices Pool");
            LOCK.readLock().lock();
            for (IndexAsNode v :remainingVertices) {
                System.out.println(v.unWrap());
            }
        }finally {
            LOCK.readLock().unlock();
        }
    }

    /**
     * displays all the vertices that were found by both the Threads that run bfs-like solution.
     */
    public void displayShared () {
        try {
            System.out.println("Shared Result Pool");
            LOCK.readLock().lock();
            for (IndexAsNode v :sharedResults.keySet()) {
                System.out.println(v.unWrap());
            }
        }finally {
            LOCK.readLock().unlock();
        }
    }

}