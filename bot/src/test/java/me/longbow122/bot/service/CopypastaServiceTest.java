package me.longbow122.bot.service;

import com.googlecode.catchexception.apis.BDDCatchException;
import jakarta.persistence.EntityNotFoundException;
import me.longbow122.bot.dto.mapper.CopypastaMapper;
import me.longbow122.datamodel.repository.CopypastaRepository;
import me.longbow122.datamodel.repository.entities.Copypasta;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;

import java.util.Optional;

import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CopypastaService.class})
public class CopypastaServiceTest {

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

	//* We are mocking the repository so that we can test the service layer without having to test the repository layer.
	//* We do not have to test the repository layer since we are testing that it works elsewhere.
	@MockBean
	private CopypastaRepository copypastaRepository;

	@Autowired
	private CopypastaService copypastaService;

	@Nested
	class CreateCopypasta {

		@Test
		void testValidInsertion_shouldPass() {
			// ? Test that inserting a record with the right information formatting works.
			Copypasta pasta = new Copypasta("test", "testDescription", "This is a message");

			when(copypastaRepository.save(any(Copypasta.class))).thenReturn(pasta);

			Copypasta created = copypastaService.createCopypasta(CopypastaMapper.toDTO(pasta));

			assertEquals("test", created.getName());
			assertEquals("testDescription", created.getDescription());
			assertEquals("This is a message", created.getMessage());
			verify(copypastaRepository, times(1)).save(any(Copypasta.class));
			verify(copypastaRepository).save(argThat(copypasta ->
				copypasta.getName().equals("test") &&
					copypasta.getDescription().equals("testDescription") &&
					copypasta.getMessage().equals("This is a message")));
			verifyNoMoreInteractions(copypastaRepository);
		}

		@Test
		void testMultipleValidInsertions_shouldPass() {
			// ? Test that inserting multiple records with the right information formatting works.
			Copypasta pasta = new Copypasta("test", "testDescription", "This is a message");
			Copypasta pasta2 = new Copypasta("testtwo", "testDescription2", "This is a message2");

			when(copypastaRepository.save(any(Copypasta.class))).thenReturn(pasta).thenReturn(pasta2);

			Copypasta created = copypastaService.createCopypasta(CopypastaMapper.toDTO(pasta));

			assertEquals("test", created.getName());
			assertEquals("testDescription", created.getDescription());
			assertEquals("This is a message", created.getMessage());
			verify(copypastaRepository).save(argThat(copypasta ->
				copypasta.getName().equals("test") &&
					copypasta.getDescription().equals("testDescription") &&
					copypasta.getMessage().equals("This is a message")));

			Copypasta created2 = copypastaService.createCopypasta(CopypastaMapper.toDTO(pasta2));

			assertEquals("testtwo", created2.getName());
			assertEquals("testDescription2", created2.getDescription());
			assertEquals("This is a message2", created2.getMessage());
			verify(copypastaRepository).save(argThat(copypasta ->
				copypasta.getName().equals("testtwo") &&
					copypasta.getDescription().equals("testDescription2") &&
					copypasta.getMessage().equals("This is a message2")));

			verify(copypastaRepository, times(2)).save(any(Copypasta.class));
			verifyNoMoreInteractions(copypastaRepository);
		}
	}

	@Nested
	class UpdateCopypasta {

		@Test
		void testUpdateName_shouldPass() {
			// ? Test that updating a Copypasta name works
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");
			Copypasta updated = new Copypasta("newName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenReturn(updated);

			copypastaService.updateCopypastaName("testName", "newName");

			verify(copypastaRepository).findCopypastaByName("testName");
			verify(copypastaRepository).save(argThat(copypasta -> copypasta.getName().equals("newName")));
		}

		@Test
		void testUpdateNameToExistingPrimaryKey_shouldFail() {
			// ? Test that updating a Copypasta name to an existing primary key does not work under the constraints
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenThrow(DataIntegrityViolationException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaName("testName", "testName"));

			verify(copypastaRepository).findCopypastaByName("testName");
			then(caughtException()).isInstanceOf(DataIntegrityViolationException.class);
		}

		@Test
		void testUpdateDescription_shouldPass() {
			// ? Test that updating a Copypasta description works
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");
			Copypasta updated = new Copypasta("testName", "newDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenReturn(updated);

			copypastaService.updateCopypastaDescription("testName", "newDescription");

			verify(copypastaRepository).findCopypastaByName("testName");
			verify(copypastaRepository).save(argThat(copypasta -> copypasta.getDescription().equals("newDescription")));
		}

		@Test
		void testUpdateMessage_shouldPass() {
			// ? Test that updating a Copypasta message works
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");
			Copypasta updated = new Copypasta("testName", "testDescription", "newMessage");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenReturn(updated);

			copypastaService.updateCopypastaMessage("testName", "newMessage");

			verify(copypastaRepository).findCopypastaByName("testName");
			verify(copypastaRepository).save(argThat(copypasta -> copypasta.getMessage().equals("newMessage")));
		}


		@Test
		void testEmptyNameUpdate_shouldFail() {
			// ? Test that updating a Copypasta to an empty name does not work with the constraints
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenThrow(TransactionSystemException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaName("testName", ""));

			verify(copypastaRepository).findCopypastaByName("testName");
			then(caughtException()).isInstanceOf(TransactionSystemException.class);
		}


		@Test
		void testLargeNameUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a name that is too large does not work with the constraints
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenThrow(DataIntegrityViolationException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaName("testName", repeatString('a', 55)));

			verify(copypastaRepository).findCopypastaByName("testName");
			then(caughtException()).isInstanceOf(DataIntegrityViolationException.class);
		}


		@Test
		void testNullNameUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a null name does not work with the constraints
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenThrow(TransactionSystemException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaName("testName", null));

			verify(copypastaRepository).findCopypastaByName("testName");
			then(caughtException()).isInstanceOf(TransactionSystemException.class);
		}

		@Test
		void testWhitespaceNameUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a whitespace name does not work with the constraints
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenThrow(TransactionSystemException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaName("testName", " "));

			verify(copypastaRepository).findCopypastaByName("testName");
			then(caughtException()).isInstanceOf(TransactionSystemException.class);
		}

		@Test
		void testEmptyDescriptionUpdate_shouldFail() {
			// ? Test that updating a Copypasta to an empty description does not work with the constraints
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenThrow(TransactionSystemException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaDescription("testName", ""));

			verify(copypastaRepository).findCopypastaByName("testName");
			then(caughtException()).isInstanceOf(TransactionSystemException.class);
		}

		@Test
		void testLargeDescriptionUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a description that is too large does not work with the constraints
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenThrow(DataIntegrityViolationException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaDescription("testName", repeatString('b', 150)));

			verify(copypastaRepository).findCopypastaByName("testName");
			then(caughtException()).isInstanceOf(DataIntegrityViolationException.class);
		}

		@Test
		void testNullDescriptionUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a null description does not work with the constraints
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenThrow(DataIntegrityViolationException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaDescription("testName", null));

			verify(copypastaRepository).findCopypastaByName("testName");
			then(caughtException()).isInstanceOf(DataIntegrityViolationException.class);
		}

		@Test
		void testWhitespaceDescriptionUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a whitespace description does not work with the constraints
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenThrow(DataIntegrityViolationException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaDescription("testName", " "));

			verify(copypastaRepository).findCopypastaByName("testName");
			then(caughtException()).isInstanceOf(DataIntegrityViolationException.class);
		}

		@Test
		void testEmptyMessageUpdate_shouldFail() {
			// ? Test that updating a Copypasta to an empty message does not work with the constraints
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenThrow(DataIntegrityViolationException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaMessage("testName", ""));

			verify(copypastaRepository).findCopypastaByName("testName");
			then(caughtException()).isInstanceOf(DataIntegrityViolationException.class);
		}

		@Test
		void testLargeMessageUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a message that is too large does not work with the constraints
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenThrow(DataIntegrityViolationException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaMessage("testName", repeatString('c', 3000)));

			verify(copypastaRepository).findCopypastaByName("testName");
			then(caughtException()).isInstanceOf(DataIntegrityViolationException.class);
		}

		@Test
		void testNullMessageUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a null message does not work with the constraints
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenThrow(TransactionSystemException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaMessage("testName", null));

			verify(copypastaRepository).findCopypastaByName("testName");
			then(caughtException()).isInstanceOf(TransactionSystemException.class);
		}

		@Test
		void testWhitespaceMessageUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a whitespace message does not work with the constraints
			Copypasta original = new Copypasta("testName", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testName")).thenReturn(Optional.of(original));
			when(copypastaRepository.save(any(Copypasta.class))).thenThrow(TransactionSystemException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaMessage("testName", " "));

			verify(copypastaRepository).findCopypastaByName("testName");
			then(caughtException()).isInstanceOf(TransactionSystemException.class);
		}

		@Test
		void testUpdateNameNameNotExists_shouldFail() {
			// ? Test that trying to update a Copypasta message with a name that does not exist fails properly
			when(copypastaRepository.findCopypastaByName("notExists")).thenThrow(EntityNotFoundException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaName("notExists", "newName"));

			verify(copypastaRepository).findCopypastaByName("notExists");
			then(caughtException()).isInstanceOf(EntityNotFoundException.class);
		}


		@Test
		void testUpdateDescriptionNameNotExists_shouldFail() {
			// ? Test that trying to update a Copypasta description with a name that does not exist fails properly
			when(copypastaRepository.findCopypastaByName("notExists")).thenThrow(EntityNotFoundException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaDescription("notExists", "newDescription"));

			verify(copypastaRepository).findCopypastaByName("notExists");
			then(caughtException()).isInstanceOf(EntityNotFoundException.class);
		}

		@Test
		void testUpdateMessageNameNotExists_shouldFail() {
			// ? Test that trying to update a Copypasta message with a name that does not exist fails properly
			when(copypastaRepository.findCopypastaByName("notExists")).thenThrow(EntityNotFoundException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaMessage("notExists", "newMessage"));

			verify(copypastaRepository).findCopypastaByName("notExists");
			then(caughtException()).isInstanceOf(EntityNotFoundException.class);
		}

		@Test
		void testUpdateNameNameNull_shouldFail() {
			// ? Test that trying to update a Copypasta name with a name that is null fails properly
			when(copypastaRepository.findCopypastaByName(null)).thenThrow(EntityNotFoundException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaName(null, "newName"));

			verify(copypastaRepository).findCopypastaByName(null);
			then(caughtException()).isInstanceOf(EntityNotFoundException.class);
		}

		@Test
		void testUpdateDescriptionNameNull_shouldFail() {
			// ? Test that trying to upddate a Copypasta description with a name that is null fails properly
			when(copypastaRepository.findCopypastaByName(null)).thenThrow(EntityNotFoundException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaDescription(null, "newDescription"));

			verify(copypastaRepository).findCopypastaByName(null);
			then(caughtException()).isInstanceOf(EntityNotFoundException.class);
		}

		@Test
		void testUpdateMessageNameNull_shouldFail() {
			// ? Test that trying to update a Copypasta message with a name that is null fails properly
			when(copypastaRepository.findCopypastaByName(null)).thenThrow(EntityNotFoundException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaMessage(null, "newMessage"));

			verify(copypastaRepository).findCopypastaByName(null);
			then(caughtException()).isInstanceOf(EntityNotFoundException.class);
		}

		@Test
		void testUpdateNameNameEmpty_shouldFail() {
			// ? Test that trying to update a Copypasta name with a name that is empty fails properly
			when(copypastaRepository.findCopypastaByName("")).thenThrow(EntityNotFoundException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaName("", "newName"));

			verify(copypastaRepository).findCopypastaByName("");
			then(caughtException()).isInstanceOf(EntityNotFoundException.class);
		}

		@Test
		void testUpdateDescriptionNameEmpty_shouldFail() {
			// ? test that trying to update a Copypasta description with a name that is empty fails properly
			when(copypastaRepository.findCopypastaByName("")).thenThrow(EntityNotFoundException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaDescription("", "newDescription"));

			verify(copypastaRepository).findCopypastaByName("");
			then(caughtException()).isInstanceOf(EntityNotFoundException.class);
		}

		@Test
		void testUpdateMessageNameEmpty_shouldFail() {
			// ? Test that trying to update a Copypasta message with a name that is empty fails properly
			when(copypastaRepository.findCopypastaByName("")).thenThrow(EntityNotFoundException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaMessage("", "newMessage"));

			verify(copypastaRepository).findCopypastaByName("");
			then(caughtException()).isInstanceOf(EntityNotFoundException.class);
		}

		@Test
		void testUpdateNameNameWhitespace_shouldFail() {
			// ? Test that trying to update a Copypasta name with a name that is a whitespace fails properly
			when(copypastaRepository.findCopypastaByName(" ")).thenThrow(EntityNotFoundException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaName(" ", "newName"));

			verify(copypastaRepository).findCopypastaByName(" ");
			then(caughtException()).isInstanceOf(EntityNotFoundException.class);
		}

		@Test
		void testUpdateDescriptionNameWhitespace_shouldFail() {
			// ? Test that trying to update a Copypasta description with a name that is a whitespace fails properly
			when(copypastaRepository.findCopypastaByName(" ")).thenThrow(EntityNotFoundException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaDescription(" ", "newDescription"));

			verify(copypastaRepository).findCopypastaByName(" ");
			then(caughtException()).isInstanceOf(EntityNotFoundException.class);
		}

		@Test
		void testUpdateMessageNameWhitespace_shouldFail() {
			// ? Test that trying to update a Copypasta message with a name that is a whitespace fails properly
			when(copypastaRepository.findCopypastaByName(" ")).thenThrow(EntityNotFoundException.class);

			BDDCatchException.when(() -> copypastaService.updateCopypastaMessage(" ", "newName"));

			verify(copypastaRepository).findCopypastaByName(" ");
			then(caughtException()).isInstanceOf(EntityNotFoundException.class);
		}
	}

	private String repeatString(char ch, int times) {
		StringBuilder builder = new StringBuilder();
		return builder.repeat(ch, times).toString();
	}
}
