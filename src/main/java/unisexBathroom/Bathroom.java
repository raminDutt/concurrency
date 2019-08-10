package unisexBathroom;

public class Bathroom {

    Object unisexLock = new Object();
    int males = 0;
    int females = 0;
    final int CAPACITY = 3;

    public void useBathroom(String name, boolean sex) throws InterruptedException {

	acquireLock(name, sex);
	System.out.println(Thread.currentThread().getName() + ": " + name
		+ " entered bathroom ...");
	System.out.println(Thread.currentThread().getName() + ": " + name
		+ " using bathroom ...");
	Thread.sleep(1000);
	releaseLock(name, sex);
    }

    public void acquireLock(String name, boolean sex)
	    throws InterruptedException {
	synchronized (unisexLock) {
	    if (sex == true) {
		while (females > 0 || males == CAPACITY) {
		    if (females > 0) {
			System.out.println(Thread.currentThread().getName()
				+ ": females are using bathroom. " + name
				+ " is waiting ...");
		    }

		    if (males == CAPACITY) {
			System.out.println(Thread.currentThread().getName()
				+ ": bathroom full capacity has reached; "
				+ CAPACITY + " male are using bathroom. "
				+ name + " is waiting ...");
		    }
		    /*System.out.println(Thread.currentThread().getName()
			    + ": ****(male, female) = (" + males + ", " + females + ")");*/
		    unisexLock.wait();
		}
		males++;
	    } else {

		while (males > 0 || females == CAPACITY) {
		    if (males > 0) {
			System.out.println(Thread.currentThread().getName()
				+ ": males are using bathroom. " + name
				+ " is waiting ...");
		    }

		    if (males == CAPACITY) {
			System.out.println(Thread.currentThread().getName()
				+ ": bathroom full capacity has reached!!! "
				+ CAPACITY + " females are using bathroom. "
				+ name + " is waiting ...");
		    }
		    /*System.out.println(Thread.currentThread().getName()
			    + ": @@@@(male, female) = (" + males + ", " + females + ")");*/
		    unisexLock.wait();
		}
		females++;
	    }
	    /*System.out.println(Thread.currentThread().getName()
		    + ": +(male, female) = (" + males + ", " + females + ")");*/
	}
    }

    public void releaseLock(String name, boolean sex)
	    throws InterruptedException {

	synchronized (unisexLock) {
	    if (sex == true) {
		males--;

	    } else {
		females--;
	    }
	    unisexLock.notifyAll();
	    System.out.println(Thread.currentThread().getName() + ": " + name
		    + " exiting bathroom ...");
	    /*System.out.println(Thread.currentThread().getName()
		    + ": -(male, female) = (" + males + ", " + females + ")");*/

	}
    }

}
