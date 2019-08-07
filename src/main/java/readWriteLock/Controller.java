package readWriteLock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Controller {

    public static void main(String[] args) {
	Controller controller = new Controller();
	controller.start();

    }

    public void start() {
	BlockOnWriteQueue<Integer> blockOnWriteQueue = new BlockOnWriteQueue<>(
		50);
	List<Callable<Void>> writers = writers(blockOnWriteQueue, 2);
	List<Callable<Void>> readers = readers(blockOnWriteQueue, 4);
	List<Callable<Void>> tasks = new ArrayList<Callable<Void>>(writers);
	tasks.addAll(readers);
	Collections.shuffle(tasks);

	ExecutorService executorService = Executors.newCachedThreadPool();

	try {
	    List<Future<Void>> results = executorService.invokeAll(tasks);
	    for(Future<Void> result: results)
	    {
		try {
		    result.get();
		} catch (ExecutionException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	
	
	executorService.shutdown();
    }

    public List<Callable<Void>> writers(
	    BlockOnWriteQueue<Integer> blockOnWriteQueue, int numberOfWriters) {
	List<Callable<Void>> writers = new ArrayList<>();
	Random generator = new Random();

	int i = 1;
	numberOfWriters++;
	while (i < numberOfWriters) {
	    String name = "writer_" + i;
	    Callable<Void> writer = () -> {
		System.out.println(Thread.currentThread().getName() + ": "
			+ name + " started ...");
		boolean isSucess = true;
		while (isSucess) {
		    try {
			isSucess = blockOnWriteQueue.write(Integer
				.valueOf(generator.nextInt(100)));
		    } catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		    }

		}
		System.out.println(Thread.currentThread().getName() + ": "
			+ name + " finished ...");
		return null;
	    };
	    writers.add(writer);
	    i++;
	}
	return writers;
    }

    public <T> List<Callable<Void>> readers(
	    BlockOnWriteQueue<T> blockOnWriteQueue, int nuberOfReaders) {
	List<Callable<Void>> readers = new ArrayList<>();
	int i = 1;
	nuberOfReaders++;
	while (i < nuberOfReaders) {
	    String name = "reader_" + i;
	    Callable<Void> task = () -> {
		System.out.println(Thread.currentThread().getName() + ": "
			+ name + " started ...");
		int numberOfReads = 0;
		while (numberOfReads < 5) {
		    int size = blockOnWriteQueue.getSize();
		    while(size==0)
		    {
			System.out.println(Thread.currentThread().getName() + ": yielding ...");
			Thread.sleep(1);
			size = blockOnWriteQueue.getSize();
		    }
		    Random generator = new Random();
		    int index = generator.nextInt(size);
		    T value = blockOnWriteQueue.read(index);
		    numberOfReads++;
		}
		System.out.println(Thread.currentThread().getName() + ": "
			+ name + " finished ...");
		return null;
	    };
	    readers.add(task);
	    i++;

	}
	return readers;
    }
}
