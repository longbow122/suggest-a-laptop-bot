package me.longbow122.api.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import me.longbow122.bot.dto.FormDTO;
import me.longbow122.bot.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@ComponentScan(basePackages = {
	"me.longbow122.bot.exception",
	"me.longbow122.bot.exception.exceptions"
})
public class FormController {

	private final FormService formService;

	@Autowired
	public FormController(FormService formService) {
		this.formService = formService;
	}

	@PostMapping("/form")
	public ResponseEntity<String> postFormAnswers(@RequestBody @Valid FormDTO form) {
		formService.postForm(form);
		return ResponseEntity.ok("Form has been posted successfully!");
	}
}