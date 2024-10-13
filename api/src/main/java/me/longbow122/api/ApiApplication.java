package me.longbow122.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationPropertiesScan("me.longbow122.configuration.properties")
@ComponentScan(basePackages = {
	"me.longbow122.datamodel.repository",
	"me.longbow122.service",
	"me.longbow122.exception",
	"me.longbow122.configuration",
	"me.longbow122.api.controller",
})
public class ApiApplication {

	//TODO FROM THERE, WE NEED TO FIND A WAY TO REMOVE RELIANCE OF THE BOT TOKEN FROM THE API JAR, ENSURING THAT WE CAN RUN UNIT TESTS WITHOUT IT.

	//TODO NEED TO FIGURE OUT DEPLOYMENT OF THE APPLICATION TO GCP OR SOME OTHER SERVICE ASAP AS WELL, TO MAKE SURE WE CAN GET IT RUNNING FOR A LIVE DEMO

	//TODO NEED TO LOOK INTO WHY THE BOT JAR IS NOT RUNNING THE BOT JAR AND THE API IS RUNNING IT AND THE BOT JAR.
	// THE API JAR IS ALSO DOING THIS ON ITS OWN, MEANING WE NEED TO INVESTIGATE MAVEN SCOPES SINCE WE HAD IT SET TO RUNTIME AND NOT COMPILE.
	//TODO MIGHT BE SOMETHING TO DO WITH THE BEAN CREATION, WHEN WE TRY AUTOWIRING IN FORMSERVICE FOR FORMCONTROLLER
	// FORMSERVICE AUTOWIRES IN DISCORDCONFIGURER, WHICH HAS THE JDA BEAN RUN ON CREATION, SO THAT WE CAN TAKE AND USE IT.
	// IF THIS BEAN IS RUNNING ON CREATION, THEN WE WILL HAVE IT COME UP AND START AN INSTANCE OF THE BOT, WHICH IS SOMETHING WE DON'T WANT TO DO.

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}
}
