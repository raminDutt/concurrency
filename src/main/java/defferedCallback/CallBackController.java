package defferedCallback;


public class CallBackController {

    public static void main(String[] args) throws InterruptedException {
	DefferedQueue defferedQueue = new DefferedQueue();
	Runnable task1 = () -> {
	    System.out.println("Executing Task 1 ... ");
	};

	Runnable task2 = () -> {
	    System.out.println("Executing Task 2 ... ");
	};
	
	Runnable task3 = () -> {
	    System.out.println("Executing Task 3 ... ");
	};
	
	    defferedQueue.start();
	    defferedQueue.scheduleEvent("task1", task1, 5);
	    defferedQueue.scheduleEvent("task2", task2, 50);
	    System.out.println(Thread.currentThread().getName() + ": sleeping...");
	    Thread.sleep(7000);
	    System.out.println(Thread.currentThread().getName() + ": adding task 3...");
	    defferedQueue.scheduleEvent("task3", task3, 10);
	    defferedQueue.shutDown();
    }
    
    

}
