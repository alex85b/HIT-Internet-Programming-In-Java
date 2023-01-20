package Part2;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * this class will provide a way for the server'
 * to handle client requests that are designed to handle binary matrix.
 * this class implement IHandler and as such will provide a handle method.
 */
public class MatrixIHandler implements IHandler {

    private MatrixAsGraph matrixAsGraph;
    private Index start, end;

    /**
     * common constructor, this will call to utility method that resets class members.
     */
    public MatrixIHandler() {
        this.resetParams();
    }

    /**
     * this utility method used to reset class members.
     */
    private void resetParams(){
        this.matrixAsGraph = null;
        this.start = null;
        this.end = null;
    }

    /**
     * this method will handle requests from the client and replays to said client.
     * @param inClient the information that is sent by the client will come trough this pipe.
     * @param outClient the server replay will come through this pipe.
     * @throws Exception
     */
    @Override
    public void handle(InputStream inClient, OutputStream outClient) throws Exception {

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outClient);
        ObjectInputStream objectInputStream = new ObjectInputStream(inClient);

        this.resetParams();

        boolean alive = true;
        while (alive) {
            switch (objectInputStream.readObject().toString()) {
                case "stop":{
                    alive= false;
                    break;
                }
                case "task 1": {
                    try {
                        int[][] primitiveMatrix = (int[][]) objectInputStream.readObject();
                        matrixAsGraph = new MatrixAsGraph(primitiveMatrix);
                        objectOutputStream.writeObject(matrixAsGraph.getConnectivityConcurrently(new threadAssignedBFS()));
                    }catch (Exception e) {
                        System.err.println("{handle::task_1} has failed");
                        objectOutputStream.writeObject( new LinkedList<HashSet<Index>>());
                    }
                    finally {
                        break;
                    }
                }
                case "task 2": {
                    try {
                        int[][] primitiveMatrix = (int[][]) objectInputStream.readObject();
                        Index S = (Index) objectInputStream.readObject();
                        Index E = (Index) objectInputStream.readObject();
                        matrixAsGraph = new MatrixAsGraph(primitiveMatrix);
                        objectOutputStream.writeObject(matrixAsGraph.getPath(new IndexAsNode(S),new IndexAsNode(E),new bfsTraverse()));
                    }catch (Exception e) {
                        System.err.println("{handle::task_2} has failed");
                        objectOutputStream.writeObject( new LinkedList<>());
                    }finally {
                        break;
                    }


                }
                case "task 3": {
                    try {
                        int[][] primitiveMatrix = (int[][]) objectInputStream.readObject();
                        submarines newGame = new submarines(primitiveMatrix);
                        objectOutputStream.writeObject(newGame.howManySubmarines());
                    }catch (Exception e) {
                        System.err.println("{handle::task_3} has failed");
                        objectOutputStream.writeObject( -1 );
                    }finally {
                        break;
                    }
                }
                default: {
                    System.err.println("This server does not provide such service");
                    break;
                }
            }
        }
    }
}