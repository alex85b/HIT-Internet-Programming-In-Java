package concepts_testing;

public class main {

    public static void main(String[] args) {

        Runnable r1 = ()-> {
            outer local = new outer();
            try {
                local.run("this");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable r2 = ()-> {
            outer local = new outer();
            try {
                local.run(null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
