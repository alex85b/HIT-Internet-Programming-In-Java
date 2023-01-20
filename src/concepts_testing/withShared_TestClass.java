package concepts_testing;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class withShared_TestClass {

    private static HashSet<String> isShared = new HashSet<>();
    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public boolean addToShared(@NotNull String addThis) {
        try {

            lock.readLock().lock();
            if(isShared.contains(addThis)) {
                System.out.println("attempt to add:{"+addThis+"} has failed.");
                System.out.println("shared contains {"+isShared+"}");
            }
            lock.readLock().unlock();
            lock.writeLock().lock();
            isShared.add(addThis);
            System.out.println(Thread.currentThread().getName()+
                    "Tries to add:{"+addThis+"}");
            System.out.println(isShared.toString());
            lock.writeLock().unlock();
            return true;

        }catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    };
}
