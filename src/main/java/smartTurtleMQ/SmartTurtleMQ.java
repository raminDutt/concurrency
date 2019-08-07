package smartTurtleMQ;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SmartTurtleMQ<T> {
    final int capacity;
    int size;
    T[] array;
    final T TERMINATION_TOKEN;
    Object lock = new Object();
    int frontIndex = 0;
    int endIndex = 0;

    int terminationTokenCount = 0;
    int TOTAL_TOKEN;
    Set<Long> producers = new HashSet<>(); 

    public SmartTurtleMQ(int capacity, T TERMINATION_TOKEN) {
	this.capacity = capacity;
	array = (T[]) new Object[capacity];
	this.TERMINATION_TOKEN = TERMINATION_TOKEN;
    }

    public void add(T message) throws InterruptedException {
	synchronized (lock) {
	    registerProducer();
	    while (size == capacity) {
		System.out.println(Thread.currentThread().getName()
			+ ": TurtleMQ is FULL");
		lock.wait();
	    }

	    array[endIndex] = message;
	    endIndex = (endIndex + 1) % capacity;
	    size++;
	    System.out.println(Thread.currentThread().getName() + ": Added = "
		    + Arrays.toString(array));
	    lock.notifyAll();
	}
    }

    private void registerProducer() {
	long id = Thread.currentThread().getId();
	if(!producers.contains(id))
	{
	    producers.add(id);
	    TOTAL_TOKEN++;
	}
		
    }

    public T get() throws InterruptedException {
	T message = null;
	synchronized (lock) {

	    while (true) {
		while (size == 0) {
		    System.out.println(Thread.currentThread().getName()
			    + ": TurtleMQ is EMPTY");
		    if (isDone()) {
			System.out
				.println(Thread.currentThread().getName()
					+ ": TurtleMQ has detected that consumers are done");
			throw new InterruptedException();
		    } else {
			System.out.println(Thread.currentThread().getName()
				+ ": Waiting ...");
			lock.wait();
		    }
		}

		message = array[frontIndex];
		array[frontIndex] = null;
		frontIndex = (frontIndex + 1) % capacity;
		size--;
		if (message != null && message.equals(TERMINATION_TOKEN)) {
		    terminationTokenCount++;
		} else {
		    System.out.println(Thread.currentThread().getName()
			    + ": Removed =" + Arrays.toString(array));
		    lock.notifyAll();
		    break;
		}
	    }
	}

	return message;
    }

    private boolean isDone() {
	if (terminationTokenCount == TOTAL_TOKEN) {
	    return true;

	}
	return false;
    }
}
