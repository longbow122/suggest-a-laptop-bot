package me.longbow122.api.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import me.longbow122.dto.FormDTO;
import me.longbow122.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@ComponentScan(basePackages = {
	"me.longbow122.exception",
	"me.longbow122.exception.exceptions"
})
public class FormController {
	//TODO HOW DO WE MOCK AND TEST THIS? SHOULD WE FIND A WAY TO PROVIDE A RESPONSE ENTITY?

	private final FormService formService;

	@Autowired
	public FormController(FormService formService) {
		this.formService = formService;
	}

	//TODO WE NEED TO WRITE TESTS FOR THIS ENDPOINT AND THE FORM DTO VALIDATION!

	@PostMapping("/form")
	public ResponseEntity<String> postFormAnswers(@RequestBody @Valid FormDTO form) {
		formService.postForm(form);
		return ResponseEntity.ok("Form has been posted successfully!");
	}
}