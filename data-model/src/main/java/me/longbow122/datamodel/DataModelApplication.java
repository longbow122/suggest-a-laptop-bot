package me.longbow122.datamodel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataModelApplication {
	//TODO DO WE NEED THIS CLASS? IF WE ARE CREATING THE FLATFILE THROUGH HERE, WE MAY HAVE TO RUN THIS JAR.
	// SEE IF WE CAN FIND A WAY TO GET THE FLATFILE DB SETUP AND RUNNING WITHOUT NEEDING TO RUN ANYTHING FROM THIS APPLICATION?
	public static void main(String[] args) {
		SpringApplication.run(DataModelApplication.class, args);
	}

}
