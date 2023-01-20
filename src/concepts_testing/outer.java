package concepts_testing;

public class outer {

    private shared isPerThread = new shared();

    public void run (String input) throws InterruptedException {

        user a = new user();
        user b = new user();
        if(input != null) {
            Runnable local1 = ()-> {
                a.use(input, isPerThread);
            };
            Runnable local2 = ()-> {
                b.use(input+"_",isPerThread);
            };
            Thread t1 = new Thread(local1);
            t1.start();
            t1.join();
            System.out.println(Thread.currentThread().getName()+" t1 finished: "+isPerThread.getShared());
            Thread t2 = new Thread(local2);
            t2.start();
            t2.join();
        }
        Thread.sleep(2000);
        System.out.println(Thread.currentThread().getName()+ " :: "+isPerThread.getShared());
        System.out.println(Thread.currentThread().getName()+" is done");
    }
}
