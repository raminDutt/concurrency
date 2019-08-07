package smartTurtleMQ;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MessageController {
    final String TERMINATION_TOKEN = "DONE";

    public void start() {

	String[] producer1_messages = {
		"Poffy",
		"Arman",
		"Ramin",
		"John",
		TERMINATION_TOKEN };
	
	String[] producer2_messages = {
		"SJC",
		"SFO",
		"LAX",
		"NYC",
		TERMINATION_TOKEN };

	SmartTurtleMQ<String> mq = new SmartTurtleMQ<String>(3, TERMINATION_TOKEN);
	Runnable producer1 = createProducer(producer1_messages, mq);
	Runnable producer2 = createProducer(producer2_messages, mq);
	Runnable consumer1 = createConsumer(mq);
	Runnable consumer2 = createConsumer(mq);

	ExecutorService executor = Executors.newCachedThreadPool();
	executor.execute(producer1);
	executor.execute(producer2);
	executor.execute(consumer1);
	executor.execute(consumer2);

	executor.shutdown();
	try {
	    executor.awaitTermination(10000, TimeUnit.SECONDS);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	System.out.println("Message Controller shutting down ...");

    }

    public Runnable createConsumer(SmartTurtleMQ<String> mq) {
	Runnable consumer = () -> {
	    System.out.println(Thread.currentThread().getName()
		    + ": Consumer starting ...");
	    while (!Thread.currentThread().isInterrupted()) {

		String message;
		try {
		    message = mq.get();

		    System.out.println(Thread.currentThread().getName()
			    + ": consumer message = " + message);

		} catch (InterruptedException e) {
		    Thread.currentThread().interrupt();
		}

	    }
	    System.out.println(Thread.currentThread().getName()
		    + ": Consumer shutting down  ...");
	};

	return consumer;
    }

    public Runnable createProducer(String[] messages, SmartTurtleMQ<String> mq) {
	Runnable producer = () -> {
	    System.out.println(Thread.currentThread().getName()
		    + ": Producer starting ...");
	    for (String message : messages) {
		try {
		    mq.add(message);
		} catch (InterruptedException e) {
		    Thread.currentThread().interrupt();
		}
	    }
	    System.out.println(Thread.currentThread().getName()
		    + ": Producer shutting down ...");
	};
	return producer;
    }

    public static void main(String[] args) {
	MessageController messageController = new MessageController();
	messageController.start();
    }

}


