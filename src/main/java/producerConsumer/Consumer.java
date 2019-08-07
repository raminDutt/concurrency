package producerConsumer;


public class Consumer<T> implements Runnable {

    TurtleMQ<T> turtleMQ;
    final T TERMINATION_FLAG;
    String name;
    volatile int terminationFlagCount = 0;

    public Consumer(String name, TurtleMQ<T> turtleMQ, T TERMINATION_FLAG) {
	this.turtleMQ = turtleMQ;
	this.name = name;
	this.TERMINATION_FLAG = TERMINATION_FLAG;
    }
    
    public int getTerminationFlagCount() {
	return terminationFlagCount;
    }

    @Override
    public void run() {
	System.out.println(name + ": Started ...");
	T message = null;
	while (true) 
	{
	    try
	    {
		System.out.println(Thread.currentThread().getName() + ": " + name + " A");
		message = turtleMQ.get();
		System.out.println(Thread.currentThread().getName() + ": " + name + " B = " + message);
		if(message != null && message.equals(TERMINATION_FLAG))
		{
		    terminationFlagCount++;
		    System.out.println(Thread.currentThread().getName() + ": " + name + ": C =  " + terminationFlagCount);
		    continue;
		}
		System.out.println(Thread.currentThread().getName() + ": " + name +  ": D = " + message);
	    }
	    catch(InterruptedException exception)
	    {
		System.out.println(Thread.currentThread().getName() + ": " + name +  ": E");
		Thread.currentThread().interrupt();
		break;
	    }
	   
	}
	System.out.println(name + ": Shutting down ...");
    }

}
