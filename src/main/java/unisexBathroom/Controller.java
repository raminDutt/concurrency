package unisexBathroom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Controller {

    public static void main(String[] args) {
	Controller controller = new Controller();
	try {
	    controller.start();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void start() throws InterruptedException {
	System.out.println(Thread.currentThread().getName() + ": controller starting ... ");
	Bathroom bathroom = new Bathroom();
	int numberOfMales = 6;
	int numberOfFemales = 6;
	List<Runnable> males = createMales(bathroom, numberOfMales);
	List<Runnable> females = createFemales(bathroom, numberOfFemales);
	List<Runnable> persons = new ArrayList<Runnable>(males);
	persons.addAll(females);
	Collections.shuffle(persons);
	ExecutorService executorService = Executors.newCachedThreadPool();
	List<Future<?>> results = new ArrayList<>();
	for (Runnable person : persons) {
	    Future<?> result = executorService.submit(person);
	    results.add(result);

	}

	int completed = 0;
	int totalPersons = numberOfFemales+numberOfMales;
	while (completed != totalPersons) 
	{
	    Thread.sleep(500);
	    completed = (int)results.stream().filter(r -> r.isDone()).count();
	   
	}
	
	executorService.shutdown();
	System.out.println(Thread.currentThread().getName() + ": controller exiting ... ");

    }

    private List<Runnable> createFemales(Bathroom bathroom, int numberOfFemales) {
	boolean sex = false;
	List<Runnable> females = create(bathroom, numberOfFemales, sex);
	return females;
    }

    public List<Runnable> createMales(Bathroom bathroom, int numberOfMales) {
	boolean sex = true;
	List<Runnable> males = create(bathroom, numberOfMales, sex);
	return males;

    }

    public List<Runnable> create(Bathroom bathroom, int numberOfMales,
	    boolean sex) {
	List<Runnable> humans = new ArrayList<Runnable>();

	int i = 0;
	while (i < numberOfMales) {
	    String name = (sex ? "Male_" : "Female_") + (i + 1);
	    Runnable human = () -> {
		try {
		    bathroom.useBathroom(name, sex);
		} catch (InterruptedException e) {
		    Thread.currentThread().interrupt();
		    e.printStackTrace();
		}
	    };
	    humans.add(human);
	    i++;
	}
	return humans;
    }

}
