package me.longbow122.datamodel.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TransactionRequiredException;
import jakarta.validation.ConstraintViolationException;
import me.longbow122.datamodel.repository.entities.Copypasta;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Copy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.TransactionSystemException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(properties = {
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"spring.datasource.url=jdbc:h2:mem:testdb"
})
public class CopypastaRepositoryTest {

	//TODO ALL OF THESE TESTS ARE NAMED QUITE BADLY, THEY SHOULD BE NAMED BETTER.

	//TODO WE NEED TO ADD EXTRA CASES FOR WHITESPACE-ONLY STRINGS

	@Autowired
	private CopypastaRepository copypastaRepository;
	@Autowired
	private LocalContainerEntityManagerFactoryBean entityManagerFactory;

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

			Copypasta pasta = new Copypasta(null, "description", "this is a message");
			assertThrows(TransactionSystemException.class, () -> copypastaRepository.save(pasta));
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testLargeNameInsertion() {
			// ? Test that inserting a name that is too large does not work with the constraints
		}

		@Test
		void testNullNameInsertion() {
			// ? Test that inserting a null name does not work with the constraints
		}

		@Test
		void testEmptyDescriptionInsertion() {
			// ? Test that inserting an empty name does not work with the constraints
		}

		@Test
		void testLargeDescriptionInsertion() {
			// ? Test that inserting a description that is too large does not work with the constraints
		}

		@Test
		void testNullDescriptionInsertion() {
			// ? Test that inserting a null name does not work with the constraints
		}

		@Test
		void testEmptyMessageInsertion() {
			// ? Test that inserting an empty message does not work with the constraints
		}

		@Test
		void testLargeMessageInsertion() {
			// ? Test that inserting a message that is too large does not work with the constraints.
		}

		@Test
		void testNullMessageInsertion() {
			// ? Test that inserting a null message does not work with the constraints
		}
		//TODO TESTS FOR CREATION HERE
		// TEST PRIMARY KEY INSERTION? MUST BE UNIQUE
		// TEST REAL INSERTION
		// TEST MULTIPLE INSERTION
		// TEST NULL INSERTION

		//TODO TEST CONSTRAINTS FOR EVERY COPYPASTA FIELD HERE TOO
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
			Copypasta pasta = copypastaRepository.findCopypastaByName("testName");
			assertEquals("testName", pasta.getName());
		}

		@Test
		void testGetAllCopypastas() {
			// ? Test that getting all Copypastas works
		}

		@Test
		void testGetCopypastaStartingWith() {
			// ? Test that getting all Copypastas starting with a string works
		}

		@Test
		void testGetCopypastaNotExists() {
			// ? Test that getting a Copypasta that should not exist fails properly.
		}



		//TODO TESTS FOR READING HERE
		// TEST GETTING EXACT COPYPASTAS
		// TEST GETTING ALL COPYPASTAS
		// TEST GETTING COPYPASTAS STARTING WITH A STRING

	}

	@Nested
	class UpdateCopypasta {

		@BeforeEach
		void init() {
			Copypasta pasta = new Copypasta("testName", "testDescription", "This is a message");
			copypastaRepository.save(pasta);
		}

		@AfterEach
		void cleanup() {
			copypastaRepository.deleteAll();
		}
		//TODO DO WE NEED TO ADD EXTRA CASES HERE FOR WHEN THE ORIGINAL COPYPASTA DOESN'T EXIST?
		//TODO WE NEED TO ADD EXTRA CASES FOR WHITESPACE-ONLY STRINGS

		@Test
		void testUpdateName() {
			// ? Test that updating a Copypasta name works
		}


		@Test
		void testUpdateNameToExistingPrimaryKey() {
			// ? Test that updating a Copypasta name to an existing primary key does not work under the constraints
		}

		@Test
		void testUpdateDescription() {
			// ? Test that updating a Copypasta description works
		}


		@Test
		void testUpdateMessage() {
			// ? Test that updating a Copypasta message works
		}

		@Test
		void testAllLargeSizeUpdate() {
			// ? Test that updating a Copypasta that breaks size constraints does not work with the constraints
		}


		@Test
		void testAllEmptyUpdate() {
			// ? Test that updating a Copypasta that has all fields empty does not work with the constraints
		}

		@Test
		void testAllNullUpdate() {
			// ? Test that updating a Copypasta that has all fields null does not work with the constraints
		}


		@Test
		void testEmptyNameUpdate() {
			// ? Test that updating a Copypasta that has an empty name does not work with the constraints
		}


		@Test
		void testLargeNameUpdate() {
			// ? Test that updating a Copypasta that has a name that is too large does not work with the constraints
		}


		@Test
		void testNullNameUpdate() {
			// ? Test that updating a Copypasta that has a null name does not work with the constraints
		}


		@Test
		void testEmptyDescriptionUpdate() {
			// ? Test that updating a Copypasta that has an empty description does not work with the constraints
		}


		@Test
		void testLargeDescriptionUpdate() {
			// ? Test that updating a Copypasta that has a description that is too large does not work with the constraints
		}

		@Test
		void testNullDescriptionUpdate() {
			// ? Test that updating a Copypasta that has a null description does not work with the constraints
		}


		@Test
		void testEmptyMessageUpdate() {
			// ? Test that updating a Copypasta that has an empty message does not work with the constraints
		}

		@Test
		void testLargeMessageUpdate() {
			// ? Test that updating a Copypasta that has a message that is too large does not work with the constraints
		}


		@Test
		void testNullMessageUpdate() {
			// ? Test that updating a Copypasta that has a null message does not work with the constraints
		}
		//TODO TESTS FOR UPDATING HERE
		// UPDATE NAME
		// UPDATE NAME TO A PRIMARY KEY THAT EXISTS
		// UPDATE FIELD
		// UPDATE VALUE

		//TODO TEST CONSTRAINTS FOR EVERY COPYPASTA FIELD HERE TOO
	}

	@Nested
	class DeleteCopypasta {
		//TODO TESTS FOR DELETION HERE
		// TEST DELETION

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
		void testDeleteCopypasta() {
			// ? Test that deleting a Copypasta by name works
		}

		@Test
		void testDeleteNotExistsCopypasta() {
			// ? Test that deleting a Copypasta that does not exist will fail.
		}
	}
}
