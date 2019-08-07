package bucketFilter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Controller {

    public static void main(String[] args) throws InterruptedException {
	Controller controller = new Controller();
	controller.start();
    }

    private void start() throws InterruptedException {

	System.out.println("Controller has started ... ");
	Bucket bucket = new Bucket(5);
	Runnable consumer1 = createConsumer(bucket);
	Runnable consumer2 = createConsumer(bucket);
	Thread.sleep(5000);
	ExecutorService executors = Executors.newCachedThreadPool();

	Future<?> consumer1_result = executors.submit(consumer1);
	Future<?> consumer2_result = executors.submit(consumer2);

	Thread.sleep(5000);

	consumer1_result.cancel(true);
	consumer2_result.cancel(true);
	executors.shutdown();
	executors.awaitTermination(10, TimeUnit.SECONDS);
	System.out.println("Controller is shutting down ... ");

    }

    int count = 1;
    ReentrantLock lock = new ReentrantLock();

    public Runnable createConsumer(Bucket bucket) {
	Runnable consumer = () -> {
	    System.out.println(Thread.currentThread().getName()
		    + ": Consumer has started ...");
	    while (true) {
		try {
		    boolean token = bucket.getToken();
		    lock.lock();
		    System.out.println(Thread.currentThread().getName()
			    + ": Consumer got token " + count);
		    count++;
		    lock.unlock();
		} catch (InterruptedException exception) {
		    Thread.currentThread().interrupt();
		    break;
		}
	    }
	    System.out.println(Thread.currentThread().getName()
		    + ": Consumer is shutting down ...");
	};
	return consumer;
    }

}
