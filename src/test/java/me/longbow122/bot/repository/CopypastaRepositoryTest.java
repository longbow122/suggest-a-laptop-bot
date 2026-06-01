package me.longbow122.bot.repository;

import me.longbow122.bot.repository.entities.Copypasta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CopypastaRepositoryTest {

	private CopypastaRepository copypastaRepository;

	@BeforeEach
	void setUp() {
		copypastaRepository = new CopypastaRepository("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1", "sa", "");
	}

	@Nested
	class CreateCopypasta {

		@Test
		void testValidInsertion_shouldPass() {
			Copypasta pasta = new Copypasta("test", "testDescription", "This is a message");
			assertEquals(0, copypastaRepository.count());

			copypastaRepository.save(pasta);

			assertEquals(1, copypastaRepository.count());
		}

		@Test
		void testMultipleValidInsertion_shouldPass() {
			Copypasta pasta = new Copypasta("testone", "testDescription", "This is a message");
			Copypasta pasta2 = new Copypasta("testtwo", "testDesc", "This is another message");
			assertEquals(0, copypastaRepository.count());

			copypastaRepository.saveAll(List.of(pasta, pasta2));

			assertEquals(2, copypastaRepository.count());
		}

		@Test
		void testPrimaryKeyCollisionInsertion_shouldPass() {
			Copypasta pasta = new Copypasta("test", "testDescription", "This is a message");
			Copypasta pasta2 = new Copypasta("test", "testDesc", "This is another message");
			assertEquals(0, copypastaRepository.count());

			copypastaRepository.save(pasta);
			copypastaRepository.save(pasta2);

			assertEquals(1, copypastaRepository.count());
			Copypasta pastaUnderName = copypastaRepository.findCopypastaByName("test").get();
			assertEquals("test", pastaUnderName.getName());
			assertEquals("testDesc", pastaUnderName.getDescription());
			assertEquals("This is another message", pastaUnderName.getMessage());
		}

		@Test
		void testNullInsertion_shouldFail() {
			assertEquals(0, copypastaRepository.count());

			assertThrows(IllegalArgumentException.class, () -> copypastaRepository.save(null));

			assertEquals(0, copypastaRepository.count());
		}
	}

	@Nested
	class GetCopypasta {

		@BeforeEach
		void init() {
			Copypasta pasta = new Copypasta("test", "testDescription", "This is a message");
			Copypasta pasta1 = new Copypasta("testa", "testDesc", "This is another message");
			Copypasta pasta2 = new Copypasta("testb", "testDesc", "This is another message");
			Copypasta pasta3 = new Copypasta("testc", "testDesc", "This is another message");
			Copypasta pasta4 = new Copypasta("testd", "testDesc", "This is another message");
			Copypasta pasta5 = new Copypasta("teste", "testDesc", "This is another message");
			Copypasta individualPasta = new Copypasta("individual", "desc", "message");
			copypastaRepository.saveAll(List.of(pasta, pasta1, pasta2, pasta3, pasta4, pasta5, individualPasta));
		}

		@Test
		void testGetCopypasta_shouldPass() {
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("test");
			assertTrue(pasta.isPresent());
			assertEquals("test", pasta.get().getName());
		}

		@Test
		void testGetAllCopypastas_shouldPass() {
			List<Copypasta> copypastaList = copypastaRepository.findAll();
			assertEquals(7, copypastaList.size());
			assertEquals(7, copypastaRepository.count());
		}

		@Test
		void testGetCopypastaStartingWith_shouldPass() {
			List<Copypasta> copypastaList = copypastaRepository.findCopypastaByNameStartingWith("test");
			assertEquals(6, copypastaList.size());
			assertEquals(7, copypastaRepository.count());
		}

		@Test
		void testGetCopypastaNotExists_shouldPass() {
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("notexisting");
			assertEquals(7, copypastaRepository.count());
			assertTrue(pasta.isEmpty());
		}

		@Test
		void testGetAllCopypastasEmptyList_shouldPass() {
			assertEquals(7, copypastaRepository.findAll().size());
			assertEquals(7, copypastaRepository.count());

			copypastaRepository.deleteAll();

			assertEquals(0, copypastaRepository.findAll().size());
			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testGetCopypastaStartingWithNotExists_shouldPass() {
			assertEquals(7, copypastaRepository.count());
			List<Copypasta> copypastaList = copypastaRepository.findCopypastaByNameStartingWith("notexisting");
			assertEquals(0, copypastaList.size());
		}

		@Test
		void testGetCopypastaWithEmptyString_shouldPass() {
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("");
			assertEquals(7, copypastaRepository.count());
			assertTrue(pasta.isEmpty());
		}

		@Test
		void testGetCopypastaWithWhitespace_shouldPass() {
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName("  ");
			assertEquals(7, copypastaRepository.count());
			assertTrue(pasta.isEmpty());
		}

		@Test
		void testGetCopypastaStartingWithEmptyString_shouldPass() {
			List<Copypasta> pasta = copypastaRepository.findCopypastaByNameStartingWith("");
			assertEquals(7, copypastaRepository.count());
			assertEquals(7, pasta.size());
		}

		@Test
		void testGetCopypastaStartingWithWhitespace_shouldPass() {
			List<Copypasta> pasta = copypastaRepository.findCopypastaByNameStartingWith("  ");
			assertEquals(7, copypastaRepository.count());
			assertEquals(0, pasta.size());
		}

		@Test
		void testGetCopypastaUsingNull_shouldPass() {
			Optional<Copypasta> pasta = copypastaRepository.findCopypastaByName(null);
			assertTrue(pasta.isEmpty());
			assertEquals(7, copypastaRepository.count());
		}

		@Test
		void testGetCopypastaStartingWithUsingNull_shouldPass() {
			List<Copypasta> pastaList = copypastaRepository.findCopypastaByNameStartingWith(null);
			assertEquals(0, pastaList.size());
			assertEquals(7, copypastaRepository.count());
		}
	}

	@Nested
	class DeleteCopypasta {

		@BeforeEach
		void init() {
			Copypasta pasta = new Copypasta("test", "testDescription", "This is a message");
			Copypasta pasta1 = new Copypasta("testa", "testDesc", "This is another message");
			Copypasta pasta2 = new Copypasta("testb", "testDesc", "This is another message");
			Copypasta pasta3 = new Copypasta("testc", "testDesc", "This is another message");
			Copypasta pasta4 = new Copypasta("testd", "testDesc", "This is another message");
			Copypasta pasta5 = new Copypasta("teste", "testDesc", "This is another message");
			copypastaRepository.saveAll(List.of(pasta, pasta1, pasta2, pasta3, pasta4, pasta5));
		}

		@Test
		void testDeleteCopypastaByName_shouldPass() {
			assertEquals(6, copypastaRepository.count());

			copypastaRepository.deleteCopypastaByNameNaturalId("test");

			assertEquals(5, copypastaRepository.count());
		}

		@Test
		void testDeleteNotExistsCopypasta_shouldFail() {
			assertEquals(6, copypastaRepository.count());
			assertEquals(0, copypastaRepository.deleteCopypastaByNameNaturalId("notexists"));
			assertEquals(6, copypastaRepository.count());
		}

		@Test
		void testDeleteAllCopypasta_shouldPass() {
			assertEquals(6, copypastaRepository.count());

			copypastaRepository.deleteAll();

			assertEquals(0, copypastaRepository.count());
		}

		@Test
		void testDeleteCopypastasByWhitespaceString_shouldFail() {
			assertEquals(6, copypastaRepository.count());
			assertEquals(0, copypastaRepository.deleteCopypastaByNameNaturalId(" "));
			assertEquals(6, copypastaRepository.count());
		}

		@Test
		void testDeleteCopypastasByNullString_shouldFail() {
			assertEquals(6, copypastaRepository.count());
			assertEquals(0, copypastaRepository.deleteCopypastaByNameNaturalId(null));
			assertEquals(6, copypastaRepository.count());
		}

		@Test
		void testDeleteCopypastasByEmptyString_shouldFail() {
			assertEquals(6, copypastaRepository.count());
			assertEquals(0, copypastaRepository.deleteCopypastaByNameNaturalId(""));
			assertEquals(6, copypastaRepository.count());
		}
	}

}
