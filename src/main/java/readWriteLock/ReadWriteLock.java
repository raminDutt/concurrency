package readWriteLock;

public class ReadWriteLock {

    Object lock = new Object();
    int readers = 0;
    int writers = 0;

    /**
     * readLock() blocks only writers. Multiple Reader Threads can access
     * critical section at the same time. Because reader threads do not modify
     * the shared data. Concurrent read access is safe.
     * 
     * @throws InterruptedException
     */
    public void readLock() throws InterruptedException {
	synchronized (lock) {
	    while (writers > 0) {
		System.out.println(Thread.currentThread().getName()
			+ ": writers are in progress. Reader waiting ... ");
		lock.wait();
	    }
	    readers++;
	    System.out.println(Thread.currentThread().getName()
		    + ": incrementing readers = " + readers);
	    System.out.println(Thread.currentThread().getName()
		    + ": reader LOCKED");
	}
    }

    public void readUnlock() {
	synchronized (lock) {
	    readers--;

	    // notify writers
	    lock.notifyAll();
	    System.out.println(Thread.currentThread().getName()
		    + ": decrementing readers = " + readers);
	    System.out.println(Thread.currentThread().getName()
		    + ": reader UNLOCKED");
	}
    }

    /**
     * writeLock() blocks all reader threads and all writer threads. Only a
     * single writer thread may access the critical section.
     * 
     * @throws InterruptedException
     */
    public void writeLock() throws InterruptedException {
	synchronized (lock) {
	    while (readers > 0 || writers > 0) {
		if (readers > 0) {
		    System.out.println(Thread.currentThread().getName()
			    + ": readers are in progress. Writer waiting ...");
		}

		if (writers > 0) {
		    System.out.println(Thread.currentThread().getName()
			    + ": writers are in progress. Writer waiting ...");
		}
		lock.wait();
	    }
	    writers++;
	    System.out.println(Thread.currentThread().getName()
		    + ": incrementing writers = " + writers);
	    System.out.println(Thread.currentThread().getName()
		    + ": writer LOCKED");
	}
    }

    public void writeUnlock() {
	synchronized (lock) {
	    writers--;
	    System.out.println(Thread.currentThread().getName()
		    + ": decrementing writers = " + writers);
	    // Notifies Readers and Writers
	    lock.notifyAll();
	    System.out.println(Thread.currentThread().getName()
		    + ": writer UNLOCKED");
	}
    }

}
