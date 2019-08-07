package bucketFilter;

public class Bucket {
    
    long producerRateTime = 1000;
    long lastGetTokenTime;
    final int capacity;
    
    int size = 0;
    public Bucket(int capacity)
    {
	lastGetTokenTime = System.currentTimeMillis();
	this.capacity=capacity;
    }
    public synchronized boolean getToken() throws InterruptedException{
	
	updateSize();
	while(size == 0)
	{
	    //wait
	    long currentTimeMillis = System.currentTimeMillis();
	    long delta = Math.subtractExact(currentTimeMillis, lastGetTokenTime);
	    long waitTime = Math.subtractExact(producerRateTime, delta);
	    System.out.println(Thread.currentThread().getName() + ": consumer wiating ...");
	    this.wait(waitTime);
	    updateSize();
	}
	
	size--;	
	lastGetTokenTime = System.currentTimeMillis();
	return true;
    }
    
    private void updateSize() {
	long currentTimeMillis = System.currentTimeMillis();
	long delta = Math.subtractExact(currentTimeMillis, lastGetTokenTime);
	int tokensAdded = (int) Math.floorDiv(delta, producerRateTime);


	/*System.out.println(Thread.currentThread().getName() + ": currentTimeMillis = " + currentTimeMillis);
	System.out.println(Thread.currentThread().getName() + ": lastGetTokenTime = " + lastGetTokenTime);
	System.out.println(Thread.currentThread().getName() + ": delta = " + delta);
	System.out.println(Thread.currentThread().getName() + ": tokensAdded = " + tokensAdded);
	*/
	size = size+tokensAdded >= capacity? capacity: size+tokensAdded;
	System.out.println(Thread.currentThread().getName() + ": buckSize = " + size);
	
    }

    
}
