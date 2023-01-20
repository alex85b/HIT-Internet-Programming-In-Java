package Part2;

import Part1.Node;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * this class wraps binary-Matrix and considers said matrix as Graph,
 * each index of matrix that contain the value 1 will be considered as a vertex.
 * this class implements the IGraph interface and as such provides connectivity discovery,
 * this class also implements the Traversable interface and as such provides a path through the graph.
 */
public class MatrixAsGraph implements IGraph<IndexAsNode>, Traversable {

    protected Matrix local;
    private HashSet<IndexAsNode> V;
    private HashMap<UUID, HashSet<IndexAsNode>> adjacency; // i could hold "E", but why ...
    private static final ThreadLocal<int[][]> pointToNeighbours = new ThreadLocal<>() {
        @Override
        protected int[][] initialValue() {
            final int[][] temp = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};
            return temp;
        }
    };
    private bfsCoordinator supervisor;

    /**
     * common constructor.
     * @param input a binary 2D array that will be the base of Matrix which will be considered as Graph.
     */
    public MatrixAsGraph(int[][] input) {
        local = new Matrix(input);
        V = new HashSet<>();
        adjacency = new HashMap<>();
        supervisor  = new bfsCoordinator();
    }

    /**
     * this method will map all the indexes in the matrix that contain the value 1,
     * to Vertices of THIS graph.
     * @return an indication if mapping has been successful.
     */
    public boolean elementsToVertices() {
        try {
            if(!V.isEmpty()) V.clear();
            for(int i = 0; i < local.howManyRows; i++) {
                int column = local.primitiveMatrix[i].length;
                for(int j = 0; j < column; j++) {
                    if(local.primitiveMatrix[i][j]==1) {
                        IndexAsNode tempNode = new IndexAsNode();
                        tempNode.wrap(new Index(i,j,local.primitiveMatrix[i][j]));
                        V.add(tempNode);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("{MatrixAsGraph::elementsToVertices} has failed.");
            System.err.println(e.getMessage());
            return false;
        }
    };

    /**
     * this method will map all the indexes in the matrix that contain the value 1,
     * to Vertices of THIS graph.
     * this method will execute Concurrent mapping:
     * 1. serial iteration of each row of primitive array.
     * 1.2 concurrent probe indices of different rows.
     * @return an indication if mapping has been successful.
     */
    public boolean elementToVerticesConcurrently() {
        try {
            ThreadPoolExecutor rowsIn2DArray = new ThreadPoolExecutor(2, 4, 1,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
            ThreadPoolExecutor indicesInRow = new ThreadPoolExecutor(2, 100, 1,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
            AtomicInteger rowsDiscovered = new AtomicInteger();
            ReentrantReadWriteLock makeAtomic = new ReentrantReadWriteLock();
            if(!V.isEmpty()) V.clear();

            int r = 0;
            local.getHowManyRows();
            while (r < local.getHowManyRows()) {
                rowsDiscovered.getAndIncrement();
                int finalR = r;
                // this will get a thread {a, b, c, d, ...}
                Runnable forEachRow = ()-> {
                    int c = 0;
                    try{
                        while (c < local.primitiveMatrix[finalR].length) {
                            // this will get a thread {1, 2, 3, 4, ...}
                            int finalC = c;
                            Runnable forEachIndex = ()-> {
                                if(local.primitiveMatrix[finalR][finalC] == 1) {
                                    IndexAsNode temp = new IndexAsNode();
                                    temp.wrap(new Index(finalR, finalC));
                                    makeAtomic.writeLock().lock();
                                    this.V.add(temp);
                                    makeAtomic.writeLock().unlock();
                                }
                            }; // END OF "FOR EACH INDEX" RUNNABLE
                            c++;
                            // will execute index discovery in different threads.
                            // lets name them the {1, 2, 3, 4, ...} thread group.
                            indicesInRow.execute(forEachIndex);
                        } // END OF "while column" loop.
                    }finally {
                        rowsDiscovered.getAndDecrement();
                    }
                }; // END OF "for each row" runnable.
                r++;
                // a single thread of {a, b, c, d, ...} will run ALL the {1, 2, 3, 4, ...}
                rowsIn2DArray.execute(forEachRow);
            }// END OF "while row" loop.
            // 1. close all Exc
            // 2. lock this Thread until results are in,
            //    don't want to run something else that depend on results.
            for (int tries = 1000; tries > 0; tries--) { // 1000 attempts to sleep.
                try {
                    Thread.sleep(125);
                    break;
                } catch (Exception ignored) {}
            }
            while(rowsDiscovered.get() != 0) { // not all indices discovered yet.
                for (int tries1 = 1000; tries1 > 0; tries1--) { // 1000 attempts to sleep.
                    try {
                        Thread.sleep(100);
                        break;
                    } catch (Exception ignored) {}
                }
            } // NO MORE NEW REQUESTS.
            indicesInRow.shutdown();
            rowsIn2DArray.shutdown();

            // lets jam this thread until all the results are in.
            while(!indicesInRow.getQueue().isEmpty() || !rowsIn2DArray.getQueue().isEmpty()) {
                for (int tries = 1000; tries > 0; tries--) { // 1000 attempts to sleep.
                    try {
                        Thread.sleep(100);
                        break;
                    } catch (Exception ignored) {}
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * mapping the neighbours of each Vertex of THIS graph,
     * a neighbour of v form V of this graph, will be any neighbour in the Matrix which has the value 1.
     * this mapping will be done concurrently:
     * 1. serial iteration of each v form V of this graph.
     * 1.2 concurrent probe of 8 neighbours of said v.
     * @return an indication if mapping has been successful.
     */
    public boolean discoverAdjacencyConcurrently() {
        try {
            ThreadPoolExecutor vInV = new ThreadPoolExecutor(2, 4, 1,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
            ThreadPoolExecutor Nv = new ThreadPoolExecutor(2, 100, 1,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
            AtomicInteger verticesDiscovered = new AtomicInteger();
            ReentrantReadWriteLock makeAtomic = new ReentrantReadWriteLock();
            if(!adjacency.isEmpty()) adjacency.clear();

            for (IndexAsNode v : V) {
                // go over each vertex : iteration will be serial, discovery will be parallel.
                //////////////////////////////////////////////////////////////////////////////
                Runnable forEachVertex = ()-> {
                    verticesDiscovered.getAndIncrement();
                    makeAtomic.writeLock().lock();
                    adjacency.put(v.getId(),new HashSet<>());
                    makeAtomic.writeLock().unlock();
                    // for each vertex, try to visit 8 neighbours concurrently.
                    ///////////////////////////////////////////////////////////
                    for(int n = 0; n < 8; n++) {
                        int finalN = n;
                        Runnable forEachNeighbour = ()-> {
                            try { // maybe there is no neighbour to visit
                                Index tempIndex = v.unWrap();
                                int x = tempIndex.row+(pointToNeighbours.get())[finalN][0];
                                int y = tempIndex.column+(pointToNeighbours.get())[finalN][1];
                                int probe = local.primitiveMatrix[x][y]; // will it break ?
                                if(probe == 1) {
                                    IndexAsNode neighbour = new IndexAsNode();
                                    neighbour.wrap(new Index(x,y));
                                    makeAtomic.writeLock().lock();
                                    HashSet<IndexAsNode> NList = adjacency.get(v.getId());
                                    NList.add(neighbour);
                                    makeAtomic.writeLock().unlock();
                                }
                            } catch (Exception ignored) {} // doesn't exist.
                        };

                        // all neighbours of current v discovered
                        if(n == 7) verticesDiscovered.getAndDecrement();
                        Nv.execute(forEachNeighbour);
                    }
                };
                vInV.execute(forEachVertex);
            }
            // give the executors a little bit of time to respond.
            //////////////////////////////////////////////////////
            for (int tries = 1000; tries > 0; tries--) { // 1000 attempts to sleep.
                try {
                    Thread.sleep(125);
                    break;
                } catch (Exception ignored) {}
            }
            while(verticesDiscovered.get() != 0) { // not all indices discovered yet.
                for (int tries1 = 1000; tries1 > 0; tries1--) { // 1000 attempts to sleep.
                    try {
                        Thread.sleep(100);
                        break;
                    } catch (Exception ignored) {}
                }
            } // NO MORE NEW REQUESTS.
            vInV.shutdown();
            Nv.shutdown();

            // lets jam this thread until all the results are in.
            while(!vInV.getQueue().isEmpty() || !Nv.getQueue().isEmpty()) {
                for (int tries = 1000; tries > 0; tries--) { // 1000 attempts to sleep.
                    try {
                        Thread.sleep(100);
                        break;
                    } catch (Exception ignored) {}
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * mapping the neighbours of each Vertex of THIS graph,
     * a neighbour of v form V of this graph, will be any neighbour in the Matrix which has the value 1.
     * @return an indication if mapping has been successful.
     */
    public boolean discoverAdjacency() {
        try {
            if(!adjacency.isEmpty()) adjacency.clear();
            if(V.isEmpty()) return false;
            for(IndexAsNode v : V) {
                HashSet<IndexAsNode> temp = local.getAdjacentIndices(v.unWrap()).stream()
                        .filter(index-> local.primitiveMatrix[index.row][index.column] == 1)
                        .map(index -> new IndexAsNode(index))
                        .collect(Collectors.toCollection(HashSet::new));
                adjacency.put(v.getId(),temp);
            }
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    /**
     * this method will produce a list of graph connectivity components.
     * will be used for "Mission 02".
     * @param method a method to traverse a graph.
     * @return connectivity components.
     */
    @Override
    public List<HashSet<IndexAsNode>> getConnectivity(TraverseLogic method) {
        try {
            if(V.isEmpty()) elementToVerticesConcurrently();
            if(adjacency.isEmpty()) discoverAdjacencyConcurrently();
            if(V.isEmpty()) {
                System.err.println("## there are no vertices ##");
                return null;
            }
            bfsTraverse bfsTraverse = (Part2.bfsTraverse) method;
            Iterator<IndexAsNode> get = V.iterator();
            supervisor.setRemainingVertices((HashSet<IndexAsNode>) V.clone());
            supervisor.setDebug(false);
            List<HashSet<IndexAsNode>> graphConnectivity = new ArrayList<>();
            do {
                get = supervisor.getRemainingVertices().iterator();
                IndexAsNode start = get.next();
                graphConnectivity.add((HashSet<IndexAsNode>) method.traverse(start,supervisor,adjacency));
            } while(supervisor.verticesRemained());
            graphConnectivity.sort(Comparator.comparingInt(HashSet::size));
            return graphConnectivity;
        } catch (Exception e) {
            System.err.println("MatrixAsGraph::getConnectivity have failed");
            System.err.println(e.toString());
        }
        return null;
    }

    /**
     * this method will produce a list of graph connectivity components,
     * this will be done concurrently:
     * 1. two different threads will get a start index and concurrently run BFS-like algorithm.
     * 1.2 concurrent discovery will handle the case in which both threads discover the same component,
     * without both threads mapping the same indexes that the other thread already discovered,
     * this will be achieved by using a Coordinator class.
     * @param method a method to traverse a graph.
     * @return connectivity components.
     */
    public List<HashSet<Index>> getConnectivityConcurrently(TraverseLogic method) {
        try {
            //1. V and Adj' SET UP.
            ///////////////////////
            supervisor.setDebug(false);
            if(V.isEmpty()) elementToVerticesConcurrently();
            if(adjacency.isEmpty()) discoverAdjacencyConcurrently();
            if(V.isEmpty()) {
                System.err.println("## there are no vertices ##");
                return new LinkedList<HashSet<Index>>();
            }

            // 2. Supervisor class and Return-List and bfs-start-index SET UP.
            //////////////////////////////////////////////////////////////////
            supervisor.setRemainingVertices((HashSet<IndexAsNode>) V.clone());
            List<HashSet<Index>> graphConnectivity = new ArrayList<>();
            ExecutorService threadPool = Executors.newFixedThreadPool(2);
            threadAssignedBFS bfs = (threadAssignedBFS) method;
            IndexAsNode S1 = null;
            IndexAsNode S2 = null;

            while(supervisor.verticesRemained()) {
                try {
                    Iterator<IndexAsNode> get = supervisor.getRemainingVertices().iterator();
                    if(get.hasNext()) {
                        S1 = get.next(); // first S
                    }
                    else S1 = null;
                    if(get.hasNext()) {
                        S2 = get.next(); // second S
                    }
                    else S2 = null;
                } catch (Exception e) {
                    System.err.println("getConnectivityConcurrently start node allocation has failed");
                }

                IndexAsNode finalS1 = S1;
                Callable runBfs1 = ()-> {
                    try {
                        if(finalS1 != null) return bfs.traverse(finalS1,supervisor,adjacency);
                        else return null;
                    } catch (Exception e) {
                        System.err.println("getConnectivityConcurrently R1 failed");
                        return null;
                    }
                };
                IndexAsNode finalS2 = S2;
                Callable runBfs2 = ()-> {
                    try {
                        if (finalS2 != null )return bfs.traverse(finalS2,supervisor,adjacency);
                        else return null;
                    } catch (Exception e) {
                        System.err.println("getConnectivityConcurrently R2 failed");
                        return null;
                    }
                };
                Future<HashSet<IndexAsNode>> bfsResults1 = threadPool.submit(runBfs1);
                Future<HashSet<IndexAsNode>> bfsResults2 = threadPool.submit(runBfs2);
                while ( !bfsResults1.isDone() && !bfsResults2.isDone()) {
                    for (int tries = 1000; tries > 0; tries--) { // 1000 attempts to sleep.
                        try {
                            Thread.sleep(125);
                            break;
                        } catch (Exception ignored) {}
                    }
                } // DONE;
                if(supervisor.splitComponentFlag()) {
                    //bfsCoordinator.displayShared();
                    graphConnectivity.add(supervisor.getSharedResults().stream().map( vertex -> vertex.unWrap())
                            .collect(Collectors.toCollection(HashSet::new)));
                    supervisor.clearSharedResults();
                    supervisor.setSplitComponentFlag(false);
                }else {
                    supervisor.clearSharedResults();
                    if(bfsResults1.get() != null) {
                        /*
                        System.out.println("Those are results of 1: "+
                                (bfsResults1.get().stream()
                                        .map(node-> node.unWrap())
                                        .collect(Collectors.toCollection(HashSet::new)).toString()));
                         */
                        graphConnectivity.add(bfsResults1.get().stream().map(vertex -> vertex.unWrap())
                                .collect(Collectors.toCollection(HashSet::new)));
                    }
                    if(bfsResults2.get() != null) {
                        /*
                        System.out.println("Those are results of 2: "+
                                (bfsResults2.get().stream()
                                        .map(node-> node.unWrap())
                                        .collect(Collectors.toCollection(HashSet::new)).toString()));
                         */
                        graphConnectivity.add(bfsResults2.get().stream().map(vertex -> vertex.unWrap())
                                .collect(Collectors.toCollection(HashSet::new)));
                    }
                }
                supervisor.clearSharedResults();
            }
            threadPool.shutdown();
            if(!graphConnectivity.isEmpty())
                graphConnectivity.sort((o1, o2) -> Integer.compare(o1.size(),o2.size()));
            //System.out.println("Connectivity discovery END "+Thread.currentThread().getName());
            return graphConnectivity;
        } catch (Exception e) {
            System.err.println("{MatrixAsGraph::getConnectivityConcurrently} discovery failed.");
            System.err.println(e.toString());
            return null;
        }
    }

    /**
     * this method displays discovered vertices.
     */
    public void displayVertices () {
        System.out.println("## Vertices ##" );
        if(V.isEmpty()) {
            System.out.println("## there are no vertices ##");
            return;
        }
        for(IndexAsNode ind : V) {
            System.out.println(ind.toString());
        }
    }

    /**
     * this method displays discovered adjacency.
     */
    public void displayAdjacency() {
        System.out.println("## Adjacency ##");
        if(V.isEmpty()) {
            System.out.println("## there are no vertices ##");
            return;
        }
        for (IndexAsNode v : V) {
            if(adjacency.get(v.getId()).isEmpty()) {
                System.out.println(v.unWrap().toString()+" ---> no one");
                continue;
            } else
            for(IndexAsNode n : adjacency.get(v.getId())) {
                System.out.println(v.unWrap().toString()+" ---> "+n.unWrap().toString());
            }
        }
    }

    /**
     * this method finds a path between start Node to End Node,
     * if no path exist an empty list will be returned.
     * @param Start the Start point of the path.
     * @param End the End path of the path.
     * @param method the Traverse method.
     * @param <T> a specific type that implement INode.
     * @return a list of all the shortest path from Provided start to End point.
     */
    @Override
    public <T extends INode> List<List<Index>> getPath(T Start, T End, TraverseLogic method) {
        if(V.isEmpty()) elementsToVertices();
        if(adjacency.isEmpty()) discoverAdjacency();
        IndexAsNode S = (IndexAsNode) Start;
        IndexAsNode E = (IndexAsNode) End;
        //System.out.println("Start ="+S.unWrap()+" ,V contains S: "+V.contains(S));
        //System.out.println("End ="+E.unWrap()+" ,V contains E: "+V.contains(E));
        if(!V.contains(S) || !V.contains(E)) return new LinkedList<>();
        if(V.isEmpty()) {
            System.err.println("## there are no vertices ##");
            return new LinkedList<>();
        }

        bfsTraverse bfs = (bfsTraverse) method;
        supervisor = new bfsCoordinator();
        supervisor.setDebug(false);
        supervisor.setRemainingVertices((HashSet<IndexAsNode>) V.clone());
        bfs.traverse(S,supervisor,adjacency);
        HashMap<UUID, bfsTraverse.bfsVertex> bfsResultTable = bfs.getBfsResultTable();
        List<Index> shortestPath = new LinkedList<>();
        List<List<Index>> shortestPaths = new LinkedList<>();
        LinkedList<List<Index>> Q = new LinkedList<>();
        IndexAsNode wrapper = new IndexAsNode();

        try {
        if(bfsResultTable.get(E.getId()).Distance == -1)// there is no path from S to E.
            return shortestPaths; // empty.
        }catch (Exception e) {
            System.err.println("can't locate E in the BFS results table");
            System.err.println(e.toString());
            return new LinkedList<>();
        }
        try {
            Index u = E.unWrap();
            shortestPath.add(u);
            do {
                if(!Q.isEmpty()){
                    shortestPath = Q.remove();
                    u = shortestPath.get(shortestPath.size()-1);
                }
                while(u.compareTo(S.unWrap()) != 0) {
                    LinkedList<Index> Nu = new LinkedList<>();
                    for (IndexAsNode n : adjacency.get(wrapper.wrap(u))) {
                        if (bfsResultTable.get(n.getId()).Distance ==
                                (bfsResultTable.get(wrapper.wrap(u)).Distance -1)) {
                            Nu.add(n.unWrap());
                        }
                    }
                    u = Nu.remove();
                    while(!Nu.isEmpty()) {
                        List<Index> tempList = new LinkedList<>();
                        tempList.addAll(shortestPath);
                        Index tempIndex = Nu.remove();
                        tempList.add(tempIndex);
                        Q.add(tempList);
                    }
                    shortestPath.add(u);
                }
                shortestPaths.add(shortestPath);
            }while (!Q.isEmpty());
        } catch (Exception e) {
            System.err.println("shortest paths discovery has failed");
            System.err.println(e.toString());
            return new LinkedList<>();
        }

        //System.out.println(shortestPaths.toString());
        return shortestPaths;
    }
}
