package defferedCallback;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefferedQueue {

    ExecutorService eventExecutor = Executors.newCachedThreadPool();;
    Future<?> eventExecutorTaskResult;

    private class Event implements Comparable<Event> {

	String name;
	long startTime;

	Runnable task;

	public Event(String name, Runnable task, long startTime) {
	    this.name = name;
	    this.task = task;
	    this.startTime = startTime;
	}

	public void execute() {

	    System.out.println(Thread.currentThread().getName() + ": ("
		    + System.currentTimeMillis() + ")" + " startTime = "
		    + startTime);
	    task.run();
	}

	@Override
	public int compareTo(Event o) {
	    if (startTime > o.startTime)
		return 1;
	    else {
		if (startTime == o.startTime) {
		    return 0;
		} else {
		    return -1;
		}
	    }
	}

	@Override
	public String toString() {
	    return name;
	}
    }

    Queue<Event> queue;

    public DefferedQueue() {
	queue = new PriorityQueue<>();
    }

    volatile AtomicBoolean shutDown = new AtomicBoolean(false);

    public void start() {

	Runnable eventExecutorTask = () -> {
	    System.out.println(Thread.currentThread().getName() + ": ("
		    + System.currentTimeMillis()
		    + ") EventExecutor starting ...");

	    exit: while (true) {
		Event event;

		synchronized (this) {

		    while (queue.isEmpty()) {

			// wait
			System.out.println(Thread.currentThread().getName()
				+ ": Event Executor is waiting ...");
			try {
			    wait();
			} catch (InterruptedException e) {
			    Thread.currentThread().interrupt();
			    System.out.println(Thread.currentThread().getName()
				    + ": EventExecutor has been interrupted");
			    break exit;
			}
		    }

		    while (!queue.isEmpty()) {
			event = queue.peek();

			long currentTime = System.currentTimeMillis();
			if (currentTime > event.startTime) {
			    event = queue.poll();
			    event.execute();
			} else {
			    try {
				long waitFor = Math.subtractExact(
					event.startTime, currentTime);
				System.out.println(Thread.currentThread()
					.getName()
					+ ": waiting for next scheduledEvent "
					+ waitFor + " milliseconds");
				wait(waitFor);
				System.out.println(Thread.currentThread()
					.getName() + ": woke up " + queue);
				if (!shutDown.get()) {
				    break;
				}

			    } catch (InterruptedException e) {
				// Thread.currentThread().interrupt();
				shutDown.set(true);
				System.out
					.println(Thread.currentThread()
						.getName()
						+ ": EventExecutor has been interrupted.");
				
				System.out
				.println(Thread.currentThread()
					.getName()
					+ ": EventExecutor will terminate after executing all registered events");
			    }
			}
		    }

		    if (shutDown.get()) {
			break exit;
		    }
		}
	    }
	};

	eventExecutorTaskResult = eventExecutor.submit(eventExecutorTask);
    }

    public void scheduleEvent(String name, Runnable task, int time) {
	long startTime = System.currentTimeMillis() + time * 1000;
	Event event = new Event(name, task, startTime);
	synchronized (this) {
	    queue.add(event);
	    notify();
	    System.out.println(Thread.currentThread().getName()
		    + ": added task " + time);
	}

    }

    public void shutDown() throws InterruptedException {
	eventExecutorTaskResult.cancel(true);
	eventExecutor.shutdown();
	while (!eventExecutor.isTerminated()) {
	    Thread.sleep(500);
	}
	System.out.println(Thread.currentThread().getName()
		+ ": EventExecutor shutting down ...");
    }

}
