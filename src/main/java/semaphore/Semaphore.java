package semaphore;

public class Semaphore {

    int usedPermits;
    int MAX_PERMITS;
    Object monitor = new Object();

    public Semaphore(int MAX_PERMITS) {

	this.MAX_PERMITS = MAX_PERMITS;
	this.usedPermits = MAX_PERMITS;
    }

    public void acquire() throws InterruptedException {

	synchronized (monitor) {

	    while (usedPermits == 0) {

		System.out.println(Thread.currentThread().getName()
			+ ": *****WAITING *****");
		monitor.wait();

	    }

	    System.out.println(Thread.currentThread().getName()
		    + ": Acquired permit " + usedPermits);
	    usedPermits--;
	    if (usedPermits == 0) {
		System.out.println(Thread.currentThread().getName()
			+ ": LOCKED");
	    }
	}

    }

    public void release() {

	synchronized (monitor) {
	    if (usedPermits < MAX_PERMITS) {
		usedPermits++;
		System.out.println(Thread.currentThread().getName()
			+ ": Releasing permit " + usedPermits);
		if (usedPermits == 1) {
		    System.out.println(Thread.currentThread().getName()
			    + ": UNLOCKED");
		}
		monitor.notify();
	    }
	}
    }

}
