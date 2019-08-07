package semaphore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Controller {

    public void start() {
	
	System.out.println("Controller started ...");
	SharedObject sharedObject = new SharedObject();
	
	
	List<Callable<Void>> tasks = createTasks(sharedObject);
	ExecutorService executorService = Executors.newCachedThreadPool();
	try {
	    List<Future<Void>> results = executorService.invokeAll(tasks);
	    for(Future<Void> result: results)
	    {
		result.get();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	executorService.shutdown();
	System.out.println("Controller shutting down ...");

    }

    public static void main(String[] args) {
	
	Controller controller = new Controller();
	controller.start();
	
    }

    public List<Callable<Void>> createTasks(SharedObject sharedObject) {
	List<Callable<Void>> tasks = new ArrayList<>();

	int i = 1;
	while (i < 5) {
	    String name = "Task_" + i;
	    Callable<Void> task = () -> {

		System.out.println(Thread.currentThread().getName() + ": "
			+ name + " started ...");
		sharedObject.criticalSection(name);
		return null;
	    };
	    tasks.add(task);
	    i++;
	}

	return tasks;
    }

}


