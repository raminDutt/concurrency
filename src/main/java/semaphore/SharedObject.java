package semaphore;

public class SharedObject {
    
    final int MAX_PERMITS = 2;
    private Semaphore semaphore = new Semaphore(MAX_PERMITS);

    
    public void criticalSection(String name) throws InterruptedException
    {
	
	semaphore.acquire();
	System.out.println(Thread.currentThread().getName() + ": Executing " + name);
	semaphore.release();
	
    }

}
