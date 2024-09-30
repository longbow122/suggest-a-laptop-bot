package me.longbow122.datamodel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataModelApplication {
	//TODO DO WE NEED THIS CLASS? IF WE ARE CREATING THE FLATFILE THROUGH HERE, WE MAY HAVE TO RUN THIS JAR.
	// SEE IF WE CAN FIND A WAY TO GET THE FLATFILE DB SETUP AND RUNNING WITHOUT NEEDING TO RUN ANYTHING FROM THIS APPLICATION?

	//TODO NEED TO LOOK INTO THIS AS A PART OF ENSURING THAT WE CAN CONNECT TO THE DB AS INTENDED, THROUGH A FLATFILE DB WHERE POSSIBLE
	// WE IDEALLY WANT TO BE ABLE TO REMOVE THIS...
	public static void main(String[] args) {
		SpringApplication.run(DataModelApplication.class, args);
	}

}
