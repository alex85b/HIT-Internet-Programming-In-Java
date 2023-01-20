package concepts_testing;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class shared {

    private String SharedResource = "default";
    private ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    /*
    private static ThreadLocal<String> SharedResource = ThreadLocal.withInitial(() -> {
        String inner = "default";
        return inner;
    });
     */


    public void changeShared (String change) {
        LOCK.writeLock().lock();
        SharedResource = change;
        //SharedResource.set(change);
        LOCK.writeLock().unlock();
    }

    public String getShared () {
        try{
            LOCK.readLock().lock();
            return SharedResource;
        }finally {
            LOCK.readLock().unlock();
        }
    }
}
