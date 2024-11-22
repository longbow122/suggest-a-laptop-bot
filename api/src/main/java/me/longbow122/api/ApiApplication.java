package me.longbow122.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationPropertiesScan("me.longbow122.bot.configuration.properties")
@ComponentScan(basePackages = {
	"me.longbow122.datamodel.repository",
	"me.longbow122.bot.service",
	"me.longbow122.bot.exception",
	"me.longbow122.bot.configuration",
	"me.longbow122.api.controller",
})
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}
}
