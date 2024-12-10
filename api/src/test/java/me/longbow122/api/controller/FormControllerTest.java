package me.longbow122.api.controller;

import me.longbow122.bot.configuration.properties.DiscordConfigurationProperties;
import me.longbow122.bot.dto.FormDTO;
import me.longbow122.bot.service.FormService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FormController.class)
@OverrideAutoConfiguration(enabled = true)
@ActiveProfiles(profiles = "test")
@MockBeans({
	@MockBean(DiscordConfigurationProperties.class)
})
class FormControllerTest {

	/*
	* Providing a new profile here to ensure that we don't attempt to call bean methods that we shouldn't be.
	* We also override automatic configuration for this test to do the same. We want to ensure that we do not attempt to create beans where we shouldn't be.
	* We then provide the needed dependencies for this MockMvc, which is required for the services we are mocking.
	 */


	/*
	 * If a test is suffixed with "shouldPass", then we are testing expected behaviour. We are giving in some input, and checking to see
	 * whether we get expected output.
	 *
	 * If a test is suffixed with "shouldFail", then we are testing unexpected behaviour. We are giving in some input, and checking to see
	 * if it fails in the way we intend for it to fail. In this event, we usually expect an exception of some sort.
	 *
	 * All tests written here are expected to pass, but their suffixing will tell you what the criteria for the test passing is.
	 *
	 * There will also be a comment with each test case describing what we are testing here.
	 */

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FormService formService;

	@Test
	void testPostFormAnswersWithValidForm_shouldPass() throws Exception {
		// ? Test that using the controller with valid input will pass.
		String valid = formDTOToJSON(new FormDTO("test", 132L, List.of("testQuestion"), List.of("testAnswer")));

		mockMvc.perform(post("/form")
			.contentType(MediaType.APPLICATION_JSON)
			.content(valid))
			.andExpect(status().isOk())
			.andExpect(content().string("Form has been posted successfully!"));

		verify(formService).postForm(any(FormDTO.class));
	}

	@Test
	void testPostFormAnswersWithInvalidForm_shouldFail() throws Exception {
		// ? Test that using the controller with entirely invalid input fails as intended. We will not even see the form.
		String notValid = formDTOToJSON(new FormDTO("", 0, Collections.emptyList(), Collections.emptyList()));

		mockMvc.perform(post("/form")
			.contentType(MediaType.APPLICATION_JSON)
			.content(notValid))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Username cannot be blank!")));
	}

	@Test
	void testPostFormAnswersWithNullPoster_shouldFail() throws Exception  {
		// ? Test that using the controller with a null poster fails as intended.
		String notValid = formDTOToJSON(new FormDTO(null, 0, List.of("testQuestion"), List.of("testAnswer")));

		mockMvc.perform(post("/form")
			.contentType(MediaType.APPLICATION_JSON)
			.content(notValid))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Username cannot be null!")));
	}

	@Test
	void testPostFormAnswersWithBlankPoster_shouldFail() throws Exception  {
		// ? Test that using the controller with a blank poster fails as intended.
		String notValid = formDTOToJSON(new FormDTO("", 0, List.of("testQuestion"), List.of("testAnswer")));

		mockMvc.perform(post("/form")
			.contentType(MediaType.APPLICATION_JSON)
			.content(notValid))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Username cannot be blank!")));
	}

	@Test
	void testPostFormAnswersWithWhitespacePoster_shouldFail() throws Exception  {
		// ? Test that using the controller with a whitespace poster fails as intended.
		String notValid = formDTOToJSON(new FormDTO(" ", 0, List.of("testQuestion"), List.of("testAnswer")));

		mockMvc.perform(post("/form")
			.contentType(MediaType.APPLICATION_JSON)
			.content(notValid))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Username cannot be blank!")));
	}

	@Test
	void testPostFormAnswersWithMismatchedQuestionAmounts_shouldFail() throws Exception {
		// ? Test that using the controller with a mismatching amount of questions fails as intended.
		String notValid = formDTOToJSON(new FormDTO("test", 0 , List.of("testQuestion", "testQuestion1"), List.of("testAnswer")));

		mockMvc.perform(post("/form")
			.contentType(MediaType.APPLICATION_JSON)
			.content(notValid))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Questions and Answers must be of the same length!")));
	}

	@Test
	void testPostFormAnswersWithMismatchedAnswerAmounts_shouldFail() throws Exception  {
		// ? Test that using the controller with a mismatching amount of answers fails as intended.
		String notValid = formDTOToJSON(new FormDTO("test", 0, List.of("testQuestion"), List.of("testAnswer", "testAnswer2")));

		mockMvc.perform(post("/form")
				.contentType(MediaType.APPLICATION_JSON)
				.content(notValid))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Questions and Answers must be of the same length!")));
	}

	@Test
	void testPostFormAnswersWithNullQuestions_shouldFail() throws Exception  {
		// ? Test that using the controller with a null set of questions fails as intended.
		String notValid = formDTOToJSON(new FormDTO("test", 0, null, List.of("testAnswer")));

		mockMvc.perform(post("/form")
				.contentType(MediaType.APPLICATION_JSON)
				.content(notValid))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Questions cannot be empty!")));
	}

	@Test
	void testPostFormAnswersWithEmptyQuestions_shouldFail() throws Exception  {
		// ? Test that using the controller with an empty set of questions fails as intended.
		String notValid = formDTOToJSON(new FormDTO("test", 0, Collections.emptyList(), List.of("testAnswer")));

		mockMvc.perform(post("/form")
				.contentType(MediaType.APPLICATION_JSON)
				.content(notValid))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Questions cannot be empty!")));
	}

	@Test
	void testPostFormAnswersWithNullAnswers_shouldFail() throws Exception  {
		// ? Test that using the controller with a null set of answers fails as expected.
		String notValid = formDTOToJSON(new FormDTO("test", 0, List.of("testQuestion"), null));

		mockMvc.perform(post("/form")
				.contentType(MediaType.APPLICATION_JSON)
				.content(notValid))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Answers cannot be empty!")));
	}

	@Test
	void testPostFormAnswersWithEmptyAnswers_shouldFail() throws Exception  {
		// ? Test that using the controller with an empty set of answers fails as expected.
		String notValid = formDTOToJSON(new FormDTO("test", 0, List.of("testQuestion"), Collections.emptyList()));

		mockMvc.perform(post("/form")
				.contentType(MediaType.APPLICATION_JSON)
				.content(notValid))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Questions and Answers must be of the same length!")));
	}

	@Test
	void testPostFormAnswersWithBothQuestionsAndAnswersNull_shouldFail() throws Exception {
		// ? Test that using the controller with a null set of questions and answers fails as expected.
		String notValid = formDTOToJSON(new FormDTO("test", 0, null, null));

		mockMvc.perform(post("/form")
				.contentType(MediaType.APPLICATION_JSON)
				.content(notValid))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Answers cannot be empty!")));
	}

	@Test
	void testPostFormAnswersWithBothQuestionsAndAnswersEmpty_shouldFail() throws Exception {
		// ? Test that using the controller with an empty set of questions and answers fails as expected.
		String notValid = formDTOToJSON(new FormDTO("test", 0, Collections.emptyList(), Collections.emptyList()));

		mockMvc.perform(post("/form")
				.contentType(MediaType.APPLICATION_JSON)
				.content(notValid))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Questions cannot be empty!")));
	}

	private String formDTOToJSON(FormDTO dto) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("poster", dto.poster());
		json.put("channelSendID", dto.channelSendID());
		json.put("questions", new JSONArray(dto.questions()));
		json.put("answers", new JSONArray(dto.answers()));
		return json.toString();
	}


}