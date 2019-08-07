package producerConsumer;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TurtleMQ<T> {

    private final int capacity = 3;

    private volatile int size = 0;
    private volatile int addIndex = 0;
    private volatile int removeIndex = 0;
    T[] array = (T[]) new Object[capacity];

    ReentrantLock monitor = new ReentrantLock();
    Condition isFull = monitor.newCondition();
    Condition isEmpty = monitor.newCondition();

    public boolean add(T message) throws InterruptedException {
	monitor.lock();
	System.out.println(Thread.currentThread().getName() + ": size =" + size);
	while (isFull()) {
	    // wait
	    System.out.println(Thread.currentThread().getName()
		    + ": Queue is Full");
	    isFull.await();
	}
	array[addIndex] = message;
	addIndex = (addIndex + 1) % capacity;
	size++;
	isEmpty.signal();
	System.out.println(Thread.currentThread().getName() + ": " + Arrays.toString(array));
	System.out.println(Thread.currentThread().getName() + ": size = " + size);
	monitor.unlock();
	return true;
    }


    private boolean isFull() {
	return size == capacity ? true : false;
    }

    public T get() throws InterruptedException {
	monitor.lock();
	System.out.println( Thread.currentThread().getName() + ": size = "+ size);
	while(isEmpty()) {
	    System.out.println(Thread.currentThread().getName()
		    + ": Queue is Empty");
	    try
	    {
		isEmpty.await();
	    }
	    catch(InterruptedException exception)
	    {
		//https://stackoverflow.com/questions/8329736/await-method-of-class-condition-not-throwing-interruptedexception
		//I had a similar issue, there was a thread waiting on a condition
		//and sending theThread.interrupt() did not work. It turned out that by mistake another thread was holding a someLock locked, 
		//so await() was blocked. After I added someLock.unlock() in proper place await() started throwing InterruptedException as expected.
		monitor.unlock();
		throw exception;
	    }
	    System.out.println( Thread.currentThread().getName() + ": woke up");
	}
	T message = array[removeIndex];
	array[removeIndex] = null;
	removeIndex = (removeIndex + 1)%capacity;
	size--;
	isFull.signal();
	System.out.println(Thread.currentThread().getName() + ": " + Arrays.toString(array));
	System.out.println( Thread.currentThread().getName() + ": size = "+ size);
	monitor.unlock();
	return message;
    }


    private boolean isEmpty() {
	return size == 0 ? true : false;
    }

}
