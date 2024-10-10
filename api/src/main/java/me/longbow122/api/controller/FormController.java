package me.longbow122.api.controller;

import jakarta.validation.Valid;
import me.longbow122.dto.FormDTO;
import me.longbow122.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FormController {
	//TODO NEED TO IMPLEMENT ENDPOINT!

	//TODO CAN WE HAVE THIS BE A REQUIRED ARGS CONSTRUCTOR TO SAFELY AUTOWIRE? UNSURE HOW WE ARE MEANT TO BE REGISTERING THE CONTROLLER FOR USE

	//TODO HOW DO WE MOCK AND TEST THIS? SHOULD WE FIND A WAY TO PROVIDE A RESPONSE ENTITY?

	//TODO NEED TO CATCH EXCEPTIONS THROWN BY POST FORM TO GIVE THE RIGHT RESPONSE IF WE FAIL!

	//TODO SHALL WE TRY MAKING A LOCAL SERVICE, INSTEAD OF USING BOT SERVICES HERE? WE NEED TO PREVENT BOT METHODS FROM RUNNING!

	private final FormService formService;

	@Autowired
	public FormController(FormService formService) {
		this.formService = formService;
	}

	//TODO CAN WE FIND A WAY TO SEND THE RIGHT MESSAGE AND THROW AND EXCEPTION IF THE VALIDATION ANNOTATION FAILS AS INTENDED?

	@PostMapping("/form")
	public ResponseEntity<String> postFormAnswers(@RequestBody @Valid FormDTO form) {
		String posterUsername = form.poster();
		if (!(formService.isUsernameInGuild(posterUsername))) {
			//TODO NEED TO GIVE A BAD RESPONSE HERE IF THE USER IS NOT IN THE SERVER!
			// HOW DO WE SEND THEM A MESSAGE IF WE HAVE A BAD REQUEST?
			return ResponseEntity.badRequest().build();
		}
		formService.postForm(form);
		return ResponseEntity.ok("Form has been posted successfully!");
	}
}