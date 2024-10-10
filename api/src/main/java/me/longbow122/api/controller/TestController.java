package me.longbow122.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

	//TODO REMOVE THIS ENTIRE CLASS ONCE EVERYTHING IS WORKING AS INTENDED!

	@GetMapping("/test")
	public String testEndpoint() {
		log.info("API WAS HIT, SEEMS TO BE WORKING!");
		return "API IS WORKING";
	}
}
