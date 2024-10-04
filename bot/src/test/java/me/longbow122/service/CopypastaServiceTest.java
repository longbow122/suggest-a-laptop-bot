package me.longbow122.service;

import com.googlecode.catchexception.apis.BDDCatchException;
import me.longbow122.datamodel.repository.CopypastaRepository;
import me.longbow122.datamodel.repository.entities.Copypasta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.transaction.TransactionSystemException;

import javax.swing.text.DefaultEditorKit;
import java.util.List;
import java.util.Optional;

import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class CopypastaServiceTest {

	//* We are mocking the repository so that we can test the service layer without having to test the repository layer.
	//* We do not have to test the repository layer since we are testing that it works elsewhere.
	@MockBean
	private CopypastaRepository copypastaRepository;

	@Autowired
	private CopypastaService copypastaService;

	@Nested
	class UpdateCopypasta {

		//TODO WE MIGHT HAVE DUPLICATE TESTS IN HERE? NEED TO LOOK INTO THINGS SOME MORE.

		// TODO UPDATE THE UNIT TESTS TO PROPERLY MOCK THE SERVICE AS EXPECTED
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


		//TODO USE THE ABOVE PASS AND FAIL CASES TO ESTABLISH A STARTING POINT FOR WHERE WE CAN START TO WORK WITH THE EXCEPTIONS WE HAVE
		// GET UNIT TESTS IMPLEMENTED!
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


			assertEquals(2, copypastaRepository.count());
			copypastaService.updateCopypastaMessage("testName", "newMessage");
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals("newMessage", pasta.get().getMessage());
			assertEquals(2, copypastaRepository.count());
		}


		@Test
		void testEmptyNameUpdate_shouldFail() {
			// ? Test that updating a Copypasta to an empty name does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaService.updateCopypastaName("testName", "");
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals("testName", pasta.get().getName());
			assertEquals(2, copypastaRepository.count());
		}


		@Test
		void testLargeNameUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a name that is too large does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(DataIntegrityViolationException.class, () -> {
				copypastaService.updateCopypastaName("testName", repeatString('a', 55));
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}


		@Test
		void testNullNameUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a null name does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaService.updateCopypastaName("testName", null);
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testWhitespaceNameUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a whitespace name does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaService.updateCopypastaName("testName", " ");
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testEmptyDescriptionUpdate_shouldFail() {
			// ? Test that updating a Copypasta to an empty description does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaService.updateCopypastaDescription("testName", "");
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testDescription", pasta.get().getDescription());
		}

		@Test
		void testLargeDescriptionUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a description that is too large does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(DataIntegrityViolationException.class, () -> {
				copypastaService.updateCopypastaDescription("testName", repeatString('b', 150));
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testDescription", pasta.get().getDescription());
		}

		@Test
		void testNullDescriptionUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a null description does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaService.updateCopypastaDescription("testName", null);
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testDescription", pasta.get().getDescription());
		}

		@Test
		void testWhitespaceDescriptionUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a whitespace description does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaService.updateCopypastaDescription("testName", " ");
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testDescription", pasta.get().getDescription());
		}

		@Test
		void testEmptyMessageUpdate_shouldFail() {
			// ? Test that updating a Copypasta to an empty message does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaService.updateCopypastaMessage("testName", "");
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testLargeMessageUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a message that is too large does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(DataIntegrityViolationException.class, () -> {
				copypastaService.updateCopypastaMessage("testName", repeatString('c', 3000));
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testNullMessageUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a null message does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaService.updateCopypastaMessage("testName", null);
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testWhitespaceMessageUpdate_shouldFail() {
			// ? Test that updating a Copypasta to a whitespace message does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaService.updateCopypastaMessage("testName", " ");
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testUpdateNameNameNotExists_shouldFail() {
			// ? Test that trying to update a Copypasta message with a name that does not exist fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaName("notExists", "newName");
			});
			assertEquals(2, copypastaRepository.count());
		}


		@Test
		void testUpdateDescriptionNameNotExists_shouldFail() {
			// ? Test that trying to update a Copypasta description with a name that does not exist fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaDescription("notExists", "newDescription");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateMessageNameNotExists_shouldFail() {
			// ? Test that trying to update a Copypasta message with a name that does not exist fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaMessage("notExists", "newMessage");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateNameNameNull_shouldFail() {
			// ? Test that trying to update a Copypasta name with a name that is null fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaName(null, "newName");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateDescriptionNameNull_shouldFail() {
			// ? Test that trying to upddate a Copypasta description with a name that is null fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaDescription(null, "newDescription");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateMessageNameNull_shouldFail() {
			// ? Test that trying to update a Copypasta message with a name that is null fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaMessage(null, "newMessage");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateNameNameEmpty_shouldFail() {
			// ? Test that trying to update a Copypasta name with a name that is empty fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaName("", "newDescription");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateDescriptionNameEmpty_shouldFail() {
			// ? test that trying to update a Copypasta description with a name that is empty fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaDescription("", "newDescription");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateMessageNameEmpty_shouldFail() {
			// ? Test that trying to update a Copypasta message with a name that is empty fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaMessage("", "newMessage");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateNameNameWhitespace_shouldFail() {
			// ? Test that trying to update a Copypasta name with a name that is a whitespace fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaName(" ", "newName");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateDescriptionNameWhitespace_shouldFail() {
			// ? Test that trying to update a Copypasta description with a name that is a whitespace fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaDescription(" ", "newDescription");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateMessageNameWhitespace_shouldFail() {
			// ? Test that trying to update a Copypasta message with a name that is a whitespace fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaMessage(" ", "newMessage");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateNameCopypastaNotExists_shouldFail() {
			// ? Test that trying to update a Copypasta that does not exist fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaName("NotExists", "newName");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateDescriptionCopypastaNotExists_shouldFail() {
			// ? Test that trying to update a Copypasta that does not exist fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaDescription("NotExists", "newDesc");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateMessageCopypastaNotExists_shouldFail() {
			// ? Test that trying to update a Copypasta that does not exist fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaService.updateCopypastaMessage("NotExists", "newMess");
			});
			assertEquals(2, copypastaRepository.count());
		}
	}

	private String repeatString(char ch, int times) {
		StringBuilder builder = new StringBuilder();
		return builder.repeat(ch, times).toString();
	}
}
