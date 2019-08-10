package unisexBathroom2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Controller {

    public void start() {
	System.out.println(Thread.currentThread().getName()
		+ ": controller starting ...");
	List<Person> males = createMales(6);
	List<Person> females = createFemales(6);
	List<Person> persons = new ArrayList<Person>(males);
	persons.addAll(females);
	Collections.shuffle(persons);
	persons.forEach(p -> System.out.print(p.getName() + " "));
	System.out.println();
	Bathroom bathroom = new Bathroom();

	bathroom.startBathroomClerk();

	for (Person person : persons) {
	    // System.out.println("Adding " + person);
	    bathroom.useBathroom(person);
	}

	try {
	    bathroom.shutdownBathroomClerk();
	    System.out.println(Thread.currentThread().getName()
		    + ": controller shutting down ...");
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	Controller controller = new Controller();
	controller.start();
    }

    public List<Person> createMales(int numberOfMales) {
	List<Person> males = createPersons(numberOfMales, Person.MALE);
	return males;
    }

    public List<Person> createFemales(int numberOfFemales) {
	List<Person> males = createPersons(numberOfFemales, Person.FEMALE);
	return males;
    }

    public List<Person> createPersons(int numberOfPersons, String sex) {
	List<Person> persons = new ArrayList<>();
	int i = 0;

	while (i < numberOfPersons) {
	    String name = sex + "_" + (i+1);
	    Person person = new Person(name, sex, new Random().nextInt(20));
	    persons.add(person);
	    i++;
	}
	return persons;
    }
}
