package Part2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * this class represents a server,
 * the server will be able to provide different "solutions" for its clients.
 */

public class aServer {

    // needs port.
    private final int port;

    // needs an on/off switch.
    private volatile boolean isWorking;
    private static ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

    /**
     * a thread safe access limitation to shared resource: the "is server still in action" boolean.
     * @return a boolean that represent the answer to the question:
     * "does the server still serves or has server-stop been triggered ?".
     */
    private boolean isWorking (){
        LOCK.readLock().lock();
        boolean local = isWorking;
        LOCK.readLock().unlock();
        return local;
    }

    // needs reusable threads to execute client's requests.
    private ThreadPoolExecutor manageThreads;

    // should receive "algorithmic solution" to client's request.
    private IHandler solutionLogic;

    /**
     * a typical constructor.
     * @param port the logical opening for communicating with the server.
     */
    // Constructor
    public aServer(int port) {
        this.port = port;
        isWorking = true;
        manageThreads = null;
    }

    /**
     * this method will set up the server by receiving a "solution" to the request of the clients,
     * and executing the Runnable "MainFunction" which contains the server logic.
     * @param solutionLogic a way to handle clients requests, extends IHandler interface.
     * @param debug a boolean that represent the answer to the question "should this display logs ?"
     */
    // sets a server mode, server will offer incoming "solution" to clients.
    public void serveSolution(IHandler solutionLogic, boolean debug) {
        this.solutionLogic = solutionLogic; // "algorithmic solution" to client's request
        if(debug)
            System.out.println("Server::Running {"+Thread.currentThread().getName()+"}");

        // as a server can switch states, depending upon the "runServer" function.
        // answering to client-requests should be independent of current state --> will run in a thread.
        ////////////////////////////////////////////////////////////////////////////////////////////////
        Runnable mainFunction = ()-> {
            try {
                if(debug)
                    System.out.println("Server::readyToServe {"+Thread.currentThread().getName()+"}");
                manageThreads = new ThreadPoolExecutor(10, 20, 10,
                        TimeUnit.SECONDS, new LinkedBlockingQueue<>());

                // establish incoming client pipeline.
                ServerSocket incomingClient = new ServerSocket(port);
                if(debug)
                    System.out.println("Server::servingClients {"+Thread.currentThread().getName()+"}");
                incomingClient.setSoTimeout(1000);
                while(isWorking()) { // as long as server-stop hasn't been triggered.
                    try {

                        // establish a request pipeline with a specific client
                        Socket requestsPipeline = incomingClient.accept();
                        if(debug)
                            System.out.println("Server::ListeningToClient "+requestsPipeline.toString());

                        // serve each request-session of a client in a different thread.
                        ////////////////////////////////////////////////////////////////
                        Runnable requestSession = ()-> {
                            try {
                                if(debug)
                                    System.out.println("Server::handleRequestSession "+Thread.currentThread());
                                solutionLogic.handle(requestsPipeline.getInputStream()
                                        ,requestsPipeline.getOutputStream());

                                // client has ended communication on his side.
                                // close communication on server side.
                                if(!requestsPipeline.isClosed()) {
                                    if(debug)
                                        System.out.println("Server::CloseInputStream "+Thread.currentThread());
                                    requestsPipeline.getInputStream().close();
                                }

                                if(!requestsPipeline.isClosed()) {
                                    if(debug)
                                        System.out.println("Server::CloseOutputStream "+Thread.currentThread());
                                    requestsPipeline.getOutputStream().close();
                                }

                                requestsPipeline.close();
                                if(debug)
                                    System.out.println("Server::EndRequestSession "+Thread.currentThread());
                            } catch (Exception e) {
                                System.err.println(Thread.currentThread().getName());
                                System.err.println("server::Error {"+e.getMessage()+"}");
                                System.err.println(e.getMessage());
                            }
                        };

                        manageThreads.execute(requestSession);
                    } catch (SocketTimeoutException ignored) {
                    }
                    catch (ClassCastException e){
                        System.err.println("Request pipeline have failed");
                        System.err.println(e.getMessage());
                        stop();
                    }
                }
                incomingClient.close();

            } catch (IOException e) {
                System.err.println("main function have failed");
                e.printStackTrace();
                stop();
            }
        };
        new Thread(mainFunction).start();
    }

    /**
     * a thread safe way to stop server from taking new requests.
     */
    public void stop(){
        if(isWorking()){
            isWorking = false;
            manageThreads.shutdown();
        }
    }
}
