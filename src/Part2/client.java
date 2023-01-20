package Part2;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * this class represent a client for my Part2 project.
 * this is a class meant for testing requested functionality,
 * this is not a requested assignment so i expect this will be IGNORED.
 */
public class client {
    // TODO: needs a socket to connect with the server.
    private Socket requestPipe;

    // TODO: needs a IP of server.
    private String IP;

    // TODO: needs a port of said server.
    private Integer port = null;

    // TODO: needs to stream to the server.
    private InputStream inputStream;
    private ObjectInputStream inputStreamDecorator;

    // TODO: needs to stream from the server.
    private OutputStream outputStream;
    private ObjectOutputStream outputStreamDecorator;

    private boolean isConnected = false;

    public ObjectInputStream inputPipe() {
        return inputStreamDecorator;
    }

    public ObjectOutputStream outputPipe() {
        return outputStreamDecorator;
    }

    // constructor
    public client(String IP, Integer port) {
        this.IP = IP;
        this.port = port;
    }

    // TODO: has to establish a connection to server.
    public boolean connectToServer () {
        try {
            requestPipe = new Socket(IP,port);
            inputStream = requestPipe.getInputStream();
            outputStream = requestPipe.getOutputStream();
            // use decorators:
            inputStreamDecorator = new ObjectInputStream(inputStream);
            outputStreamDecorator = new ObjectOutputStream(outputStream);

            isConnected = true;
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // TODO: needs the ability to close connection.
    public boolean fin () {
        try {
            outputStreamDecorator.writeObject("stop");
            inputStreamDecorator.close();
            outputStreamDecorator.close();
            requestPipe.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
