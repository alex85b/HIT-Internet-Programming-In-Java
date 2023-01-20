package Part2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * this in an interface that meant to group Objects that intended to handle client requests,
 * will be used with a server to offer Services to clients.
 * this will require group members to implement handle request method.
 */
public interface IHandler {
    /**
     * this method will handle client requests.
     * @param inClient the information that is sent by the client will come trough this pipe.
     * @param outClient the server replay will come through this pipe.
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws Exception
     */
    public void handle(InputStream inClient, OutputStream outClient) throws IOException, ClassNotFoundException, Exception;
}