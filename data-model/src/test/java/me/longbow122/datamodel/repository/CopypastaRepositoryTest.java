package me.longbow122.datamodel.repository;

import me.longbow122.datamodel.repository.entities.Copypasta;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.transaction.TransactionSystemException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"spring.datasource.url=jdbc:h2:mem:testdb"
})
public class CopypastaRepositoryTest {

	//TODO ALL OF THESE TESTS ARE NAMED QUITE BADLY, THEY SHOULD BE NAMED BETTER.

	@Autowired
	private CopypastaRepository copypastaRepository;

	@Nested
	class CreateCopypasta {

		@BeforeEach
		@AfterEach
		void cleanupAndInit() {
			copypastaRepository.deleteAll();
		}

		@Test
		void testValidInsertion() {
			// ? Test that inserting a record with the right information formatting works
			Copypasta pasta = new Copypasta("testName", "testDescription", "This is a message");
			assertEquals(0, copypastaRepository.count());

			copypastaRepository.save(pasta);

			assertEquals(1, copypastaRepository.count());
		}

		@Test
		void testMultipleValidInsertion() {
			// ? Test that inserting multiple records with the right information works.
			Copypasta pasta = new Copypasta("testName", "testDescription", "This is a message");
			Copypasta pasta2 = new Copypasta("testName1", "testDesc", "This is another message");
			assertEquals(0, copypastaRepository.count());

			copypastaRepository.saveAll(List.of(pasta, pasta2));

			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testPrimaryKeyCollisionInsertion() {
			// ? Test that inserting a record with the same primary key as an existing record does not work.
			Copypasta pasta = new Copypasta("testName", "testDescription", "This is a message");
			Copypasta pasta2 = new Copypasta("testName", "testDesc", "This is another message");
			assertEquals(0, copypastaRepository.count());
			copypastaRepository.save(pasta);
			assertEquals(1, copypastaRepository.count());
			assertThrows(DataIntegrityViolationException.class, () -> copypastaRepository.save(pasta2));
			assertEquals(1, copypastaRepository.count());
		}

		@Test
		void testAllLargeSizeInsertion() {
			// ? Test that inserting a Copypasta that breaks size constraints does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta(StringUtils.repeat("a", 50), StringUtils.repeat("b", 150), StringUtils.repeat("c", 3000));
			assertThrows(DataIntegrityViolationException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testAllEmptyInsertion() {
			// ? Test that inserting a Copypasta that has all fields empty does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("", "", "");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testAllNullInsertion() {
			// ? Test that inserting a Copypasta that has all fields null does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta(null, null, null);
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testEmptyNameInsertion() {
			// ? Test that inserting an empty name does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("", "description", "this is a message");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testLargeNameInsertion() {
			// ? Test that inserting a name that is too large does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta(StringUtils.repeat("a", 50), "description", "this is a message");
			assertThrows(DataIntegrityViolationException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testNullNameInsertion() {
			// ? Test that inserting a null name does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta(null, "description", "this is a message");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testEmptyDescriptionInsertion() {
			// ? Test that inserting an empty name does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", "", "this is a message");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testLargeDescriptionInsertion() {
			// ? Test that inserting a description that is too large does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", StringUtils.repeat("b", 150), "this is a message");
			assertThrows(DataIntegrityViolationException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testNullDescriptionInsertion() {
			// ? Test that inserting a null name does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", null, "this is a message");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testEmptyMessageInsertion() {
			// ? Test that inserting an empty message does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", "description", "");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testLargeMessageInsertion() {
			// ? Test that inserting a message that is too large does not work with the constraints.
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", "description", StringUtils.repeat("c", 3000));
			assertThrows(DataIntegrityViolationException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testNullMessageInsertion() {
			// ? Test that inserting a null message does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", "description", null);
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testAllWhitespaceOnlyInsertion() {
			// ? Test that inserting whitespace-only for all fields does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta(" ", " ",  " ");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testWhitespaceOnlyNameInsertion() {
			// ? Test that inserting a whitespace-only name does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta(" ", "description", "this is a message");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testWhitespaceOnlyDescriptionInsertion() {
			// ? Test that inserting a whitespace-only description does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", " ", "this is a message");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testWhitespaceOnlyMessageInsertion() {
			// ? Test that inserting a whitespace-only message does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", "description", " ");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}
	}

	@Nested
	class GetCopypasta {

		@BeforeEach
		void init() {
			Copypasta pasta = new Copypasta("testName", "testDescription", "This is a message");
			Copypasta pasta1 = new Copypasta("testName1", "testDesc", "This is another message");
			Copypasta pasta2 = new Copypasta("testName2", "testDesc", "This is another message");
			Copypasta pasta3 = new Copypasta("testName3", "testDesc", "This is another message");
			Copypasta pasta4 = new Copypasta("testName4", "testDesc", "This is another message");
			Copypasta pasta5 = new Copypasta("testName5", "testDesc", "This is another message");
			Copypasta individualPasta = new Copypasta("individualName", "desc", "message");
			copypastaRepository.saveAll(List.of(pasta, pasta1, pasta2, pasta3, pasta4, pasta5, individualPasta));
		}

		@AfterEach
		void cleanup() {
			copypastaRepository.deleteAll();
		}

		@Test
		void testGetCopypasta() {
			// ? Test that getting a Copypasta by name works
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testGetAllCopypastas() {
			// ? Test that getting all Copypastas works
			List<Copypasta> copypastaList = copypastaRepository.findAll();
			assertEquals(7, copypastaList.size());
			assertEquals(7, copypastaRepository.count());
		}

		@Test
		void testGetCopypastaStartingWith() {
			// ? Test that getting all Copypastas starting with a string works
			List<Copypasta> copypastaList = copypastaRepository.findCopypastaByNameStartingWith("testName");
			assertEquals(6, copypastaList.size());
			assertEquals(7, copypastaRepository.count());
		}

		@Test
		void testGetCopypastaNotExists() {
			// ? Test that getting a Copypasta that should not exist fails properly.
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("notExisting");
			assertEquals(7, copypastaRepository.count());
			assertTrue(pasta.isEmpty());
		}

		@Test
		void testGetAllCopypastasEmptyList() {
			// ? Test that getting all Copypastas from an empty repository provides an empty list
			assertEquals(7, copypastaRepository.findAll().size());
			assertEquals(7, copypastaRepository.count());
			copypastaRepository.deleteAll();
			assertEquals(0, copypastaRepository.findAll().size());
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testGetCopypastaStartingWithNotExists() {
			// ? Test that getting all Copypastas starting with a string provides an empty List when it cannot find anything.
			assertEquals(7, copypastaRepository.count());
			List<Copypasta> copypastaList = copypastaRepository.findCopypastaByNameStartingWith("NotExisting");
			assertEquals(0, copypastaList.size());
		}

		@Test
		void testGetCopypastaWithEmptyString() {
			// ? Test that getting a Copypasta with an empty string fails properly.
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("");
			assertEquals(7, copypastaRepository.count());
			assertTrue(pasta.isEmpty());
		}

		@Test
		void testGetCopypastaWithWhitespace() {
			// ? Test that getting a Copypasta with a whitespace string fails properly.
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("  ");
			assertEquals(7, copypastaRepository.count());
			assertTrue(pasta.isEmpty());
		}

		@Test
		void testGetCopypastaStartingWithEmptyString() {
			// ? Test that getting a Copypasta with an empty string gets everything.
			List<Copypasta> pasta = copypastaRepository.findCopypastaByNameStartingWith("");
			assertEquals(7, copypastaRepository.count());
			assertEquals(7, pasta.size());
		}


		@Test
		void testGetCopypastaStartingWithWhitespace() {
			// ? Test that getting a Copypasta with a whitespace string fails properly.
			List<Copypasta> pasta = copypastaRepository.findCopypastaByNameStartingWith("  ");
			assertEquals(7, copypastaRepository.count());
			assertEquals(0, pasta.size());
		}

		@Test
		void testGetCopypastaUsingNull() {
			// ? Test that getting a single Copypasta when searching with null fails properly.
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName(null);
			assertTrue(pasta.isEmpty());
			assertEquals(7, copypastaRepository.count());
		}

		@Test
		void testGetCopypastaStartingWithUsingNull() {
			// ? Test that getting several Copypastas when searching will null fails properly
			List<Copypasta> pastaList = copypastaRepository.findCopypastaByNameStartingWith(null);
			assertEquals(0, pastaList.size());
			assertEquals(7, copypastaRepository.count());
		}

	}

	@Nested
	class UpdateCopypasta {

		@BeforeEach
		void init() {
			Copypasta pasta = new Copypasta("testName", "testDescription", "This is a message");
			Copypasta pasta1 = new Copypasta("testName1", "testDesc", "This is another message");
			copypastaRepository.saveAll(List.of(pasta, pasta1));
		}

		@AfterEach
		void cleanup() {
			copypastaRepository.deleteAll();
		}


		@Test
		void testUpdateName() {
			// ? Test that updating a Copypasta name works
			assertEquals(2, copypastaRepository.count());
			copypastaRepository.updateCopypastaNameByName("testName", "newName");
			assertEquals(2, copypastaRepository.count());
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("newName");
			assertTrue(pasta.isPresent());
			assertEquals("newName", pasta.get().getName());
		}


		@Test
		void testUpdateNameToExistingPrimaryKey() {
			// ? Test that updating a Copypasta name to an existing primary key does not work under the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(DataIntegrityViolationException.class, () -> {
				copypastaRepository.updateCopypastaNameByName("testName", "testName1");
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals("testName", pasta.get().getName());
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateDescription() {
			// ? Test that updating a Copypasta description works
			assertEquals(2, copypastaRepository.count());
			copypastaRepository.updateCopypastaDescriptionByName("testName", "newDescription");
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals("newDescription", pasta.get().getDescription());
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateMessage() {
			// ? Test that updating a Copypasta message works
			assertEquals(2, copypastaRepository.count());
			copypastaRepository.updateCopypastaMessageByName("testName", "newMessage");
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals("newMessage", pasta.get().getMessage());
			assertEquals(2, copypastaRepository.count());
		}


		@Test
		void testEmptyNameUpdate() {
			// ? Test that updating a Copypasta to an empty name does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaRepository.updateCopypastaNameByName("testName", "");
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals("testName", pasta.get().getName());
			assertEquals(2, copypastaRepository.count());
		}


		@Test
		void testLargeNameUpdate() {
			// ? Test that updating a Copypasta to a name that is too large does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(DataIntegrityViolationException.class, () -> {
				copypastaRepository.updateCopypastaNameByName("testName", StringUtils.repeat("a", 55));
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}


		@Test
		void testNullNameUpdate() {
			// ? Test that updating a Copypasta to a null name does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaRepository.updateCopypastaNameByName("testName", null);
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testWhitespaceNameUpdate() {
			// ? Test that updating a Copypasta to a whitespace name does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaRepository.updateCopypastaNameByName("testName", " ");
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testEmptyDescriptionUpdate() {
			// ? Test that updating a Copypasta to an empty description does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaRepository.updateCopypastaDescriptionByName("testName", "");
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testDescription", pasta.get().getDescription());
		}

		@Test
		void testLargeDescriptionUpdate() {
			// ? Test that updating a Copypasta to a description that is too large does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(DataIntegrityViolationException.class, () -> {
				copypastaRepository.updateCopypastaDescriptionByName("testName", StringUtils.repeat("b", 150));
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testDescription", pasta.get().getDescription());
		}

		@Test
		void testNullDescriptionUpdate() {
			// ? Test that updating a Copypasta to a null description does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaRepository.updateCopypastaDescriptionByName("testName", null);
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testDescription", pasta.get().getDescription());
		}

		@Test
		void testWhitespaceDescriptionUpdate() {
			// ? Test that updating a Copypasta to a whitespace description does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaRepository.updateCopypastaDescriptionByName("testName", " ");
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testDescription", pasta.get().getDescription());
		}

		@Test
		void testEmptyMessageUpdate() {
			// ? Test that updating a Copypasta to an empty message does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaRepository.updateCopypastaMessageByName("testName", "");
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testLargeMessageUpdate() {
			// ? Test that updating a Copypasta to a message that is too large does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(DataIntegrityViolationException.class, () -> {
				copypastaRepository.updateCopypastaMessageByName("testName", StringUtils.repeat("c", 3000));
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testNullMessageUpdate() {
			// ? Test that updating a Copypasta to a null message does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaRepository.updateCopypastaMessageByName("testName", null);
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testWhitespaceMessageUpdate() {
			// ? Test that updating a Copypasta to a whitespace message does not work with the constraints
			assertEquals(2, copypastaRepository.count());
			assertThrows(TransactionSystemException.class, () -> {
				copypastaRepository.updateCopypastaMessageByName("testName", " ");
			});
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals(2, copypastaRepository.count());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testUpdateNameNameNotExists() {
			// ? Test that trying to update a Copypasta message with a name that does not exist fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaNameByName("notExists", "newName");
			});
			assertEquals(2, copypastaRepository.count());
		}


		@Test
		void testUpdateDescriptionNameNotExists() {
			// ? Test that trying to update a Copypasta description with a name that does not exist fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaDescriptionByName("notExists", "newDescription");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateMessageNameNotExists() {
			// ? Test that trying to update a Copypasta message with a name that does not exist fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaMessageByName("notExists", "newMessage");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateNameNameNull() {
			// ? Test that trying to update a Copypasta name with a name that is null fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaNameByName(null, "newName");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateDescriptionNameNull() {
			// ? Test that trying to upddate a Copypasta description with a name that is null fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaDescriptionByName(null, "newDescription");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateMessageNameNull() {
			// ? Test that trying to update a Copypasta message with a name that is null fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaMessageByName(null, "newMessage");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateNameNameEmpty() {
			// ? Test that trying to update a Copypasta name with a name that is empty fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaNameByName("", "newDescription");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateDescriptionNameEmpty() {
			// ? test that trying to update a Copypasta description with a name that is empty fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaDescriptionByName("", "newDescription");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateMessageNameEmpty() {
			// ? Test that trying to update a Copypasta message with a name that is empty fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaMessageByName("", "newMessage");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateNameNameWhitespace() {
			// ? Test that trying to update a Copypasta name with a name that is a whitespace fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaNameByName(" ", "newName");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateDescriptionNameWhitespace() {
			// ? Test that trying to update a Copypasta description with a name that is a whitespace fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaDescriptionByName(" ", "newDescription");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateMessageNameWhitespace() {
			// ? Test that trying to update a Copypasta message with a name that is a whitespace fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaMessageByName(" ", "newMessage");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateNameCopypastaNotExists() {
			// ? Test that trying to update a Copypasta that does not exist fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaNameByName("NotExists", "newName");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateDescriptionCopypastaNotExists() {
			// ? Test that trying to update a Copypasta that does not exist fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaDescriptionByName("NotExists", "newDesc");
			});
			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testUpdateMessageCopypastaNotExists() {
			// ? Test that trying to update a Copypasta that does not exist fails properly
			assertEquals(2, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> {
				copypastaRepository.updateCopypastaMessageByName("NotExists", "newMess");
			});
			assertEquals(2, copypastaRepository.count());
		}
	}

	@Nested
	class DeleteCopypasta {

		@BeforeEach
		void init() {
			Copypasta pasta = new Copypasta("testName", "testDescription", "This is a message");
			Copypasta pasta1 = new Copypasta("testName1", "testDesc", "This is another message");
			Copypasta pasta2 = new Copypasta("testName2", "testDesc", "This is another message");
			Copypasta pasta3 = new Copypasta("testName3", "testDesc", "This is another message");
			Copypasta pasta4 = new Copypasta("testName4", "testDesc", "This is another message");
			Copypasta pasta5 = new Copypasta("testName5", "testDesc", "This is another message");
			copypastaRepository.saveAll(List.of(pasta, pasta1, pasta2, pasta3, pasta4, pasta5));
		}

		@AfterEach
		void cleanup() {
			copypastaRepository.deleteAll();
		}

		@Test
		void testDeleteCopypastaByName() {
			// ? Test that deleting a Copypasta by name works
			assertEquals(6, copypastaRepository.count());
			copypastaRepository.deleteCopypastaByName("testName");
			assertEquals(5, copypastaRepository.count());
		}

		@Test
		void testDeleteNotExistsCopypasta() {
			// ? Test that deleting a Copypasta that does not exist will fail.
			assertEquals(6, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> copypastaRepository.deleteCopypastaByName("notExists"));
			assertEquals(6, copypastaRepository.count());
		}

		@Test
		void testDeleteAllCopypasta() {
			// ? Test that deleting all Copypasta works
			assertEquals(6, copypastaRepository.count());
			copypastaRepository.deleteAll();
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testDeleteCopypastasByWhitespaceString() {
			// ? Test that deleting all Copypasta by whitespace string works
			assertEquals(6, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> copypastaRepository.deleteCopypastaByName(" "));
			assertEquals(6, copypastaRepository.count());
		}

		@Test
		void testDeleteCopypastasByNullString() {
			// ? Test that deleting all Copypasta by null string works
			assertEquals(6, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> copypastaRepository.deleteCopypastaByName(null));
			assertEquals(6, copypastaRepository.count());
		}


		@Test
		void testDeleteCopypastasByEmptyString() {
			// ? Test that deleting all Copypasta by empty string works
			assertEquals(6, copypastaRepository.count());
			assertThrows(JpaObjectRetrievalFailureException.class, () -> copypastaRepository.deleteCopypastaByName(""));
			assertEquals(6, copypastaRepository.count());
		}
	}
}