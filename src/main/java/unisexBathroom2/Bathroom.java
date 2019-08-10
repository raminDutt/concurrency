package unisexBathroom2;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class Bathroom {

    private BlockingQueue<Person> queue = new LinkedBlockingQueue<Person>();
    private ExecutorService executorService = Executors.newCachedThreadPool();
    Future<?> clerk;

    public String NONE = "none";
    String occupency = NONE;
    int occupencyCounter = 0;
    final int CAPACITY = 3;

    private Object bathroomLock = new Object();

    public void startBathroomClerk() {
	Runnable clerkTask = getClerkTask();
	clerk = executorService.submit(clerkTask);
    }

    private Runnable getClerkTask() {

	Runnable task = () -> {
	    System.out.println(Thread.currentThread().getName()
		    + ": Bathroom clerk starting ...");
	    while (true) {
		try {
		    Person person = queue.take();
		    process(person);
		} catch (InterruptedException e) {
		    System.out.println(Thread.currentThread().getName()
			    + ": interupted ...");

		    while (!queue.isEmpty()) {
			Person person = queue.poll();
			if (person != null) {

			    try {
				process(person);
			    } catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
				e1.printStackTrace();
			    }
			}
		    }

		    Thread.currentThread().interrupt();
		    break;

		}

	    }
	};

	return task;
    }

    public void process(Person person) throws InterruptedException {
	if (occupency.equals(NONE)) {
	    occupency = person.getGender();
	}

	if (person.getGender().equals(occupency)) {

	    synchronized (bathroomLock) {
		while (occupencyCounter == CAPACITY) {
		    System.out.println(Thread.currentThread().getName()
			    + ": FULL CAPACITY reached. " + person.getName()
			    + " is waiting ...");
		    bathroomLock.wait();
		}
		occupencyCounter++;
		System.out.println(Thread.currentThread().getName() + ": "
			+ person.getName() + " has ENTERED the bathroom ...");
	    }

	    occupyBathroom(person);

	} else {

	    synchronized (bathroomLock) {
		while (occupencyCounter > 0) {
		    System.out.println(Thread.currentThread().getName() + ": "
			    + person.getName()
			    + " waiting ... (occupencyCounter="
			    + occupencyCounter + ")");
		    bathroomLock.wait();
		}
		occupencyCounter++;
		System.out.println(Thread.currentThread().getName() + ": "
			+ person.getName() + " has ENTERED the bathroom ...");
	    }
	    occupency = person.getGender();
	    occupyBathroom(person);

	}
    }

    private void occupyBathroom(Person person) {
	Runnable occupyBathroom = () -> {
	    System.out.println(Thread.currentThread().getName() + ": "
		    + person.getName() + " is using the bathroom ...");
	    try {
		Thread.sleep(person.getBathroomTime());
	    } catch (InterruptedException e) {
		Thread.currentThread().interrupt();
		e.printStackTrace();
	    }
	    synchronized (bathroomLock) {
		occupencyCounter--;
		bathroomLock.notify();
		System.out.println(Thread.currentThread().getName() + ": "
			+ person.getName() + " has LEFT the bathroom ...");
	    }
	};
	executorService.submit(occupyBathroom);

    }

    public void shutdownBathroomClerk() throws InterruptedException {

	while (!queue.isEmpty()) {
	    Thread.sleep(1000);
	}
	clerk.cancel(true);
	executorService.shutdown();
	while (!executorService.isTerminated()) {
	    Thread.sleep(1000);
	}
	System.out.println(Thread.currentThread().getName()
		+ ": Bathroom clerk shutting down ...");
    }

    public void useBathroom(Person person) {

	queue.add(person);
	// System.out.println(queue);
    }

}
