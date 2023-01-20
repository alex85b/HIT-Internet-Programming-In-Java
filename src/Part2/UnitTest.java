package Part2;

import java.io.*;
import java.util.concurrent.*;

/**
 * this class is a Unit Test Class,
 * it is not included in the requirements of the assignment and as such, i expect it to be IGNORED.
 */
public class UnitTest {

    // DATA
    ///////
    public static int[][] TC1_5x5_all0 = { //
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0}
    };

    public static int[][] TC2_5x5_1in3x3_in_the_middle = { //
                //0  1  2  3  4  5
            /*0*/{0, 0, 0, 0, 0, 0},
            /*1*/{0, 0, 0, 0, 0, 0},
            /*2*/{0, 0, 1, 1, 1, 0},
            /*3*/{0, 0, 1, 1, 1, 0},
            /*4*/{0, 0, 1, 1, 1, 0},
            /*5*/{0, 0, 0, 0, 0, 0}
    };

    public static int[][] TC3_5x5_single1 = { //
            {1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0}
    };

    public static int[][] TC4_5x5_3paths = { //
            {0, 0, 1, 1, 1, 0},
            {0, 1, 0, 0, 1, 0},
            {1, 0, 0, 1, 0, 0},
            {1, 1, 1, 0, 0, 0},
            {1, 0, 1, 0, 0, 0},
            {1, 1, 1, 0, 0, 0}
    };

    public static int[][] TC5_empty = { //
    };

    public static int[][] TC6_2xEmpty = { //
            {},
            {}
    };

    public static int[][] TC7_asymmetric = { //
            {1,1,0},
            {},
            {1}
    };

    public static int[][] TC8_5x5_5components = { //
            {1, 1, 1, 0, 0, 1},
            {0, 0, 0, 0, 0, 1},
            {0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 1},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 1}
    };

    // Actions
    //////////
    public static boolean task1 (int TC, String command, int[][] send, aServer server, client client) {
        try {
            System.out.println("### "+TC+" STARTING ###");
            client.outputPipe().writeObject(command);
            client.outputPipe().writeObject(send);
            System.out.println(TC+" Results are:\n"+client.inputPipe().readObject());
            System.out.println("### "+TC+" ENDED ###");
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }

    public static boolean task2 (int TC, Index S, Index E, String command, int[][] send, aServer server, client client) {
        try {
            System.out.println("### "+TC+" STARTING ###");
            client.outputPipe().writeObject(command);
            client.outputPipe().writeObject(send);
            client.outputPipe().writeObject(S);
            client.outputPipe().writeObject(E);
            System.out.println(TC+" Results are:\n"+client.inputPipe().readObject());
            System.out.println("### "+TC+" ENDED ###");
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }

    public static boolean task3 (int TC, String command, int[][] send, aServer server, client client) {
        try {
            System.out.println("### "+TC+" STARTING ###");
            client.outputPipe().writeObject(command);
            client.outputPipe().writeObject(send);
            System.out.println(TC+" Results are:\n"+client.inputPipe().readObject());
            System.out.println("### "+TC+" ENDED ###");
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }

    public static void main (String[] args) throws IOException, ClassNotFoundException {

        // simulate a server.
        aServer server = new aServer(8010);
        server.serveSolution(new MatrixIHandler(),false);

        /*
        client client1 = new client("127.0.0.1",8010);
        client client2 = new client("127.0.0.1",8010);
        if(client1.connectToServer())System.out.println("Client1::Connection to server has been established");
        if(client2.connectToServer())System.out.println("Client2::Connection to server has been established");
        // ALL WORKED.
        task1(1,"task 1",TC1_5x5_all0,server,client1);
        task1(2,"task 1",TC1_5x5_all0,server,client2);
        task1(1,"task 1",TC2_5x5_1in3x3_in_the_middle,server,client1);
        task1(2,"task 1",TC2_5x5_1in3x3_in_the_middle,server,client2);
        task1(1,"task 1",TC3_5x5_single1,server,client1);
        task1(2,"task 1",TC3_5x5_single1,server,client2);
        task1(1,"task 1",TC4_5x5_3paths,server,client1);
        task1(2,"task 1",TC4_5x5_3paths,server,client2);
        task1(1,"task 1",TC5_empty,server,client1);
        task1(2,"task 1",TC5_empty,server,client2);
        task1(1,"task 1",TC6_2xEmpty,server,client1);
        task1(2,"task 1",TC6_2xEmpty,server,client2);
        task1(1,"task 1",TC7_asymmetric,server,client1);
        task1(2,"task 1",TC7_asymmetric,server,client2);
        task1(1,"task 1",TC8_5x5_5components,server,client1);
        task1(2,"task 1",TC8_5x5_5components,server,client2);
        client1.fin();
        client2.fin();
         */

        /*
        // ALL WORKED.
        int i = 1000;
        while(i > 0){
            client client = new client("127.0.0.1",8010);
            try {
                if(!client.connectToServer()) throw new Exception();
                task1(1,"task 1",TC8_5x5_5components,server,client);

            }catch (Exception e){
                System.err.println(e.toString());
                System.err.println(i+" iterations left");
            }
            finally {
                client.fin();
            }
        }
         */

        /*
        // ALL WORKED
        Runnable tes1 = ()->{
            client client = new client("127.0.0.1",8010);
            try {
                if(!client.connectToServer()) throw new Exception();
                task1(1,"task 1",TC8_5x5_5components,server,client);

            }catch (Exception e){
                System.err.println(e.toString());
            }
            finally {
                client.fin();
            }
        };
        Runnable tes2 = ()->{
            client client = new client("127.0.0.1",8010);
            try {
                if(!client.connectToServer()) throw new Exception();
                task1(2,"task 1",TC4_5x5_3paths,server,client);

            }catch (Exception e){
                System.err.println(e.toString());
            }
            finally {
                client.fin();
            }
        };
        Runnable tes3 = ()->{
            client client = new client("127.0.0.1",8010);
            try {
                if(!client.connectToServer()) throw new Exception();
                task1(3,"task 1",TC7_asymmetric,server,client);

            }catch (Exception e){
                System.err.println(e.toString());
            }
            finally {
                client.fin();
            }
        };

        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        threadPool.submit(tes1);
        threadPool.submit(tes2);
        threadPool.submit(tes3);

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */


        Runnable tes1 = ()->{
            client client = new client("127.0.0.1",8010);
            try {
                if(!client.connectToServer()) throw new Exception();
                task2(1,new Index(0,0),new Index(0,0),
                        "task 2",TC8_5x5_5components,server,client);

            }catch (Exception e){
                System.err.println(e.toString());
            }
            finally {
                client.fin();
            }
        };
        Runnable tes2 = ()->{
            client client = new client("127.0.0.1",8010);
            try {
                if(!client.connectToServer()) throw new Exception();
                task2(2,new Index(1,1),new Index(5,5),
                        "task 2",TC8_5x5_5components,server,client);

            }catch (Exception e){
                System.err.println(e.toString());
            }
            finally {
                client.fin();
            }
        };
        Runnable tes3 = ()->{
            client client = new client("127.0.0.1",8010);
            try {
                if(!client.connectToServer()) throw new Exception();
                task1(3,"task 1",TC8_5x5_5components,server,client);

            }catch (Exception e){
                System.err.println(e.toString());
            }
            finally {
                client.fin();
            }
        };
        Runnable tes4 = ()->{
            client client = new client("127.0.0.1",8010);
            try {
                if(!client.connectToServer()) throw new Exception();
                task2(4,new Index(5,0),new Index(2,3),
                        "task 2",TC4_5x5_3paths,server,client);

            }catch (Exception e){
                System.err.println(e.toString());
            }
            finally {
                client.fin();
            }
        };
        Runnable tes5 = ()->{
            client client = new client("127.0.0.1",8010);
            try {
                if(!client.connectToServer()) throw new Exception();
                task2(5,new Index(4,2),new Index(2,4),
                        "task 2",TC2_5x5_1in3x3_in_the_middle,server,client);

            }catch (Exception e){
                System.err.println(e.toString());
            }
            finally {
                client.fin();
            }
        };
        Runnable tes6 = ()->{
            client client = new client("127.0.0.1",8010);
            try {
                if(!client.connectToServer()) throw new Exception();
                task3(6, "task 3",TC8_5x5_5components,server,client);

            }catch (Exception e){
                System.err.println(e.toString());
            }
            finally {
                client.fin();
            }
        };


        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        threadPool.submit(tes1);
        threadPool.submit(tes2);
        threadPool.submit(tes3);
        threadPool.submit(tes4);
        threadPool.submit(tes5);
        threadPool.submit(tes6);
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.stop();
        System.out.println("Unit Test Done");
    }
}

