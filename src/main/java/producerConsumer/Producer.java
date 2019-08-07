package producerConsumer;

import java.util.Queue;

public class Producer<T> implements Runnable {

    String name;
    TurtleMQ<T> turtleMQ;
    T[] messages;

    Producer(String name, TurtleMQ<T> queue, T[] messages) {
	this.name = name;
	this.turtleMQ = queue;
	this.messages = messages;
    }

    @Override
    public void run() {
	System.out.println(name + ": Started ...");
	int i = 0;
	while (i < messages.length) {
	    try {
		turtleMQ.add(messages[i]);
	    } catch (InterruptedException e) {
		Thread.currentThread().interrupt();
	    }
	    System.out.println(name + " added message: " + messages[i]);
	    i++;
	}
	System.out.println(name + " has finished ");

    }

}
