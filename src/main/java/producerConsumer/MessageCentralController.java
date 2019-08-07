package producerConsumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.plaf.SliderUI;

public class MessageCentralController {
    public static void main(String[] args) {

	TurtleMQ<String> turtleMQ = new TurtleMQ<String>();
	final String TERMINATION_FLAG = "DONE";
	// Producer 1
	String[] producer1_messages = {
		"Ramin",
		"Poffy",
		"Aynaz",
		"Roghieh",
		"Morgan",
		"Java",
		"Hibernate",
		"Spring",
		"Jersey",
		"c++",
		"Python",
		TERMINATION_FLAG };
	Producer<String> producer1 = new Producer<>("producer1", turtleMQ,
		producer1_messages);

	// Producer 2
	String[] producer2_messages = {
		"Montreal",
		"San Fransisco",
		"Los Angeles",
		"San Jose",
		"New York",
		"London",
		"Paris",
		"Toronto",
		"Chicago",
		"New Orleans",
		TERMINATION_FLAG };
	Producer<String> producer2 = new Producer<>("producer2", turtleMQ,
		producer2_messages);

	// Running Producers and Consumers
	ExecutorService executors = Executors.newCachedThreadPool();
	executors.execute(producer1);
	executors.execute(producer2);

	Consumer<String> consumer_1 = new Consumer<String>("consumer1",
		turtleMQ, TERMINATION_FLAG);
	
	Consumer<String> consumer_2 = new Consumer<String>("consumer2",
		turtleMQ, TERMINATION_FLAG);

	Future<?> consumer1_result = executors.submit(consumer_1);
	Future<?> consumer2_result = executors.submit(consumer_2);
	
	int numberOfProducers = 2;
	while (true) {

	    
	    int total = consumer_1.getTerminationFlagCount() + consumer_2.getTerminationFlagCount();
	    System.out.println(Thread.currentThread().getName() + ": " + total);
	    if (total == numberOfProducers) {
		boolean cancel1 = consumer1_result.cancel(true);
		boolean cancel2 = consumer2_result.cancel(true);
		System.out.println(Thread.currentThread().getName() + ": " + cancel1);
		System.out.println(Thread.currentThread().getName() + ": " + cancel2);
		if (cancel1) {
		    executors.shutdown();
		    System.out.println(Thread.currentThread().getName() + ": shutting down executors...");
		    break;
		}
		
	    } else {
		try {
		    Thread.sleep(500);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	}

	System.out
		.println("MessageCentralController: All consumers have been shutdown");

    }
}
