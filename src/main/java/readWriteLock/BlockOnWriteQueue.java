package readWriteLock;

import java.util.Arrays;

public class BlockOnWriteQueue<T> {

    final int CAPACITY;
    volatile int size;
    volatile T[] array;

    ReadWriteLock readWriteLock = new ReadWriteLock();

    BlockOnWriteQueue(int capacity) {
	this.CAPACITY = capacity;
	array = (T[]) new Object[CAPACITY];
	size = 0;
    }

    public boolean write(T message) throws InterruptedException {

	boolean writeStatus = false;
	readWriteLock.writeLock();
	if (size < CAPACITY) {
	    array[size] = message;
	    System.out.println(Thread.currentThread().getName() + ": writter "
		    + Arrays.toString(array));
	    size++;
	    writeStatus = true;
	}
	readWriteLock.writeUnlock();
	return writeStatus;
    }

    public T read(int index) throws InterruptedException {
	T value = null;
	readWriteLock.readLock();
	if (index < size) {

	    value = array[index];
	    System.out.println(Thread.currentThread().getName() + ": array["+index+"]=" + value);
	}
	readWriteLock.readUnlock();

	return value;

    }

    public int getSize()
    {
	return size;
    }
}
