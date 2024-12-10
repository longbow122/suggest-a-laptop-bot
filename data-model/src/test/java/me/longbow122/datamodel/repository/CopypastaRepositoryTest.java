package me.longbow122.datamodel.repository;

import me.longbow122.datamodel.repository.entities.Copypasta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.TransactionSystemException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"spring.datasource.url=jdbc:h2:mem:testdb"
})
public class CopypastaRepositoryTest {

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
	private CopypastaRepository copypastaRepository;

	@Nested
	class CreateCopypasta {

		@BeforeEach
		@AfterEach
		void cleanupAndInit() {
			copypastaRepository.deleteAll();
		}

		@Test
		void testValidInsertion_shouldPass() {
			// ? Test that inserting a record with the right information formatting works
			Copypasta pasta = new Copypasta("testName", "testDescription", "This is a message");
			assertEquals(0, copypastaRepository.count());

			copypastaRepository.save(pasta);

			assertEquals(1, copypastaRepository.count());
		}

		@Test
		void testMultipleValidInsertion_shouldPass() {
			// ? Test that inserting multiple records with the right information works.
			Copypasta pasta = new Copypasta("testName", "testDescription", "This is a message");
			Copypasta pasta2 = new Copypasta("testName1", "testDesc", "This is another message");
			assertEquals(0, copypastaRepository.count());

			copypastaRepository.saveAll(List.of(pasta, pasta2));

			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testPrimaryKeyCollisionInsertion_shouldPass() {
			// ? Test that inserting a record with the same primary key as an existing record works, and overrides/upserts.

			// * Worth noting that we are not failing the transaction here since upsert behaviour is allowed at the repository layer.
			// * Service layers will prevent primary key collisions. As such, no assertions on throwing exceptions here.
			Copypasta pasta = new Copypasta("testName", "testDescription", "This is a message");
			Copypasta pasta2 = new Copypasta("testName", "testDesc", "This is another message");
			assertEquals(0, copypastaRepository.count());
			copypastaRepository.save(pasta);
			assertEquals(1, copypastaRepository.count());
			copypastaRepository.save(pasta2);
			assertEquals(1, copypastaRepository.count());
		}

		@Test
		void testAllLargeSizeInsertion_shouldFail() {
			// ? Test that inserting a Copypasta that breaks size constraints does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta(repeatString('a', 50), repeatString('b', 150), repeatString('c', 3000));
			assertThrows(DataIntegrityViolationException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testAllEmptyInsertion_shouldFail() {
			// ? Test that inserting a Copypasta that has all fields empty does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("", "", "");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testAllNullInsertion_shouldFail() {
			// ? Test that inserting a Copypasta that has all fields null does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta(null, null, null);
			assertThrows(JpaSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testEmptyNameInsertion_shouldFail() {
			// ? Test that inserting an empty name does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("", "description", "this is a message");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testLargeNameInsertion_shouldFail() {
			// ? Test that inserting a name that is too large does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta(repeatString('a', 50), "description", "this is a message");
			assertThrows(DataIntegrityViolationException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testNullNameInsertion_shouldFail() {
			// ? Test that inserting a null name does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta(null, "description", "this is a message");
			assertThrows(JpaSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testEmptyDescriptionInsertion_shouldFail() {
			// ? Test that inserting an empty name does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", "", "this is a message");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testLargeDescriptionInsertion_shouldFail() {
			// ? Test that inserting a description that is too large does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", repeatString('b', 150), "this is a message");
			assertThrows(DataIntegrityViolationException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testNullDescriptionInsertion_shouldFail() {
			// ? Test that inserting a null name does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", null, "this is a message");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testEmptyMessageInsertion_shouldFail() {
			// ? Test that inserting an empty message does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", "description", "");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testLargeMessageInsertion_shouldFail() {
			// ? Test that inserting a message that is too large does not work with the constraints.
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", "description", repeatString('c', 3000));
			assertThrows(DataIntegrityViolationException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testNullMessageInsertion_shouldFail() {
			// ? Test that inserting a null message does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", "description", null);
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testAllWhitespaceOnlyInsertion_shouldFail() {
			// ? Test that inserting whitespace-only for all fields does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta(" ", " ",  " ");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testWhitespaceOnlyNameInsertion_shouldFail() {
			// ? Test that inserting a whitespace-only name does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta(" ", "description", "this is a message");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testWhitespaceOnlyDescriptionInsertion_shouldFail() {
			// ? Test that inserting a whitespace-only description does not work with the constraints
			assertEquals(0, copypastaRepository.count());

			Copypasta pasta = new Copypasta("name", " ", "this is a message");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testWhitespaceOnlyMessageInsertion_shouldFail() {
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
		void testGetCopypasta_shouldPass() {
			// ? Test that getting a Copypasta by name works
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("testName");
			assertTrue(pasta.isPresent());
			assertEquals("testName", pasta.get().getName());
		}

		@Test
		void testGetAllCopypastas_shouldPass() {
			// ? Test that getting all Copypastas works
			List<Copypasta> copypastaList = copypastaRepository.findAll();
			assertEquals(7, copypastaList.size());
			assertEquals(7, copypastaRepository.count());
		}

		@Test
		void testGetCopypastaStartingWith_shouldPass() {
			// ? Test that getting all Copypastas starting with a string works
			List<Copypasta> copypastaList = copypastaRepository.findCopypastaByNameStartingWith("testName");
			assertEquals(6, copypastaList.size());
			assertEquals(7, copypastaRepository.count());
		}

		@Test
		void testGetCopypastaNotExists_shouldPass() {
			// ? Test that getting a Copypasta that should not exist fails properly.
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("notExisting");
			assertEquals(7, copypastaRepository.count());
			assertTrue(pasta.isEmpty());
		}

		@Test
		void testGetAllCopypastasEmptyList_shouldPass() {
			// ? Test that getting all Copypastas from an empty repository provides an empty list
			assertEquals(7, copypastaRepository.findAll().size());
			assertEquals(7, copypastaRepository.count());
			copypastaRepository.deleteAll();
			assertEquals(0, copypastaRepository.findAll().size());
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testGetCopypastaStartingWithNotExists_shouldPass() {
			// ? Test that getting all Copypastas starting with a string provides an empty List when it cannot find anything.
			assertEquals(7, copypastaRepository.count());
			List<Copypasta> copypastaList = copypastaRepository.findCopypastaByNameStartingWith("NotExisting");
			assertEquals(0, copypastaList.size());
		}

		@Test
		void testGetCopypastaWithEmptyString_shouldPass() {
			// ? Test that getting a Copypasta with an empty string fails properly.
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("");
			assertEquals(7, copypastaRepository.count());
			assertTrue(pasta.isEmpty());
		}

		@Test
		void testGetCopypastaWithWhitespace_shouldPass() {
			// ? Test that getting a Copypasta with a whitespace string fails properly.
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("  ");
			assertEquals(7, copypastaRepository.count());
			assertTrue(pasta.isEmpty());
		}

		@Test
		void testGetCopypastaStartingWithEmptyString_shouldPass() {
			// ? Test that getting a Copypasta with an empty string gets everything.
			List<Copypasta> pasta = copypastaRepository.findCopypastaByNameStartingWith("");
			assertEquals(7, copypastaRepository.count());
			assertEquals(7, pasta.size());
		}


		@Test
		void testGetCopypastaStartingWithWhitespace_shouldPass() {
			// ? Test that getting a Copypasta with a whitespace string fails properly.
			List<Copypasta> pasta = copypastaRepository.findCopypastaByNameStartingWith("  ");
			assertEquals(7, copypastaRepository.count());
			assertEquals(0, pasta.size());
		}

		@Test
		void testGetCopypastaUsingNull_shouldPass() {
			// ? Test that getting a single Copypasta when searching with null fails properly.
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName(null);
			assertTrue(pasta.isEmpty());
			assertEquals(7, copypastaRepository.count());
		}

		@Test
		void testGetCopypastaStartingWithUsingNull_shouldPass() {
			// ? Test that getting several Copypastas when searching will null fails properly
			List<Copypasta> pastaList = copypastaRepository.findCopypastaByNameStartingWith(null);
			assertEquals(0, pastaList.size());
			assertEquals(7, copypastaRepository.count());
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
		void testDeleteCopypastaByName_shouldPass() {
			// ? Test that deleting a Copypasta by name works
			assertEquals(6, copypastaRepository.count());
			copypastaRepository.deleteCopypastaByNameNaturalId("testName");
			assertEquals(5, copypastaRepository.count());
		}

		@Test
		void testDeleteNotExistsCopypasta_shouldFail() {
			// ? Test that deleting a Copypasta that does not exist will fail.
			assertEquals(6, copypastaRepository.count());
			assertEquals(0, copypastaRepository.deleteCopypastaByNameNaturalId("notExists"));
			assertEquals(6, copypastaRepository.count());
		}

		@Test
		void testDeleteAllCopypasta_shouldPass() {
			// ? Test that deleting all Copypasta works
			assertEquals(6, copypastaRepository.count());
			copypastaRepository.deleteAll();
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testDeleteCopypastasByWhitespaceString_shouldFail() {
			// ? Test that deleting a Copypasta when passing in a whitespace string fails properly
			assertEquals(6, copypastaRepository.count());
			assertEquals(0, copypastaRepository.deleteCopypastaByNameNaturalId(" "));
			assertEquals(6, copypastaRepository.count());
		}

		@Test
		void testDeleteCopypastasByNullString_shouldFail() {
			// ? Test that deleting a Copypasta when passing in a null string fails properly
			assertEquals(6, copypastaRepository.count());
			assertEquals(0, copypastaRepository.deleteCopypastaByNameNaturalId(null));
			assertEquals(6, copypastaRepository.count());
		}


		@Test
		void testDeleteCopypastasByEmptyString_shouldFail() {
			// ? Test that deleting a Copypasta when passing in an empty string fails properly
			assertEquals(6, copypastaRepository.count());
			assertEquals(0, copypastaRepository.deleteCopypastaByNameNaturalId(""));
			assertEquals(6, copypastaRepository.count());
		}
	}

	private String repeatString(char ch, int times) {
		StringBuilder builder = new StringBuilder();
		return builder.repeat(ch, times).toString();
	}
}
