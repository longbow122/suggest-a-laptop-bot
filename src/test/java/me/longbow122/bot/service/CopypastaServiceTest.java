package me.longbow122.bot.service;

import me.longbow122.bot.repository.CopypastaRepository;
import me.longbow122.bot.repository.entities.Copypasta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class CopypastaServiceTest {

	private CopypastaRepository copypastaRepository;

	private CopypastaService copypastaService;

  @BeforeEach
  void setUp() {
    copypastaRepository = mock(CopypastaRepository.class);
    copypastaService = new CopypastaService(copypastaRepository);
  }

	@Nested
	class CreateCopypasta {

		@Test
		void testValidInsertion_shouldPass() {
			Copypasta pasta = new Copypasta("test", "testDescription", "This is a message");

      when(copypastaRepository.existsById("test")).thenReturn(false);
			when(copypastaRepository.save(any(Copypasta.class))).thenReturn(pasta);

      Copypasta created = copypastaService.createCopypasta(pasta);

			assertEquals("test", created.getName());
			assertEquals("testDescription", created.getDescription());
			assertEquals("This is a message", created.getMessage());
      verify(copypastaRepository).existsById("test");
			verify(copypastaRepository).save(argThat(copypasta ->
				copypasta.getName().equals("test") &&
					copypasta.getDescription().equals("testDescription") &&
					copypasta.getMessage().equals("This is a message")));
			verifyNoMoreInteractions(copypastaRepository);
		}

		@Test
		void testPrimaryKeyCollisionInsertion_shouldFail() {
			Copypasta pasta = new Copypasta("test", "testDescription", "This is a message");

      when(copypastaRepository.existsById("test")).thenReturn(true);

      assertThrows(IllegalStateException.class, () -> copypastaService.createCopypasta(pasta));

      verify(copypastaRepository).existsById("test");
      verify(copypastaRepository, never()).save(any(Copypasta.class));
			verifyNoMoreInteractions(copypastaRepository);
		}
	}

	@Nested
	class DeleteCopypasta {

		@Test
		void testValidDeletion_shouldPass() {
			when(copypastaRepository.deleteCopypastaByNameNaturalId("test")).thenReturn(1);

			copypastaService.deleteCopypasta("test");

			verify(copypastaRepository).deleteCopypastaByNameNaturalId("test");
			verifyNoMoreInteractions(copypastaRepository);
		}

		@Test
		void testDeletionNameNotExists_shouldFail() {
      when(copypastaRepository.deleteCopypastaByNameNaturalId("test")).thenReturn(0);

      assertThrows(NoSuchElementException.class, () -> copypastaService.deleteCopypasta("test"));

			verify(copypastaRepository).deleteCopypastaByNameNaturalId("test");
      verifyNoMoreInteractions(copypastaRepository);
    }
  }

  @Nested
  class GetCopypasta {

    @Test
    void testFindAll_shouldPass() {
      List<Copypasta> copypastas = List.of(new Copypasta("test", "description", "message"));

      when(copypastaRepository.findAll()).thenReturn(copypastas);

      assertEquals(copypastas, copypastaService.findAllCopypasta());
      verify(copypastaRepository).findAll();
      verifyNoMoreInteractions(copypastaRepository);
    }

    @Test
    void testFindByPrefix_shouldPass() {
      List<Copypasta> copypastas = List.of(new Copypasta("test", "description", "message"));

      when(copypastaRepository.findCopypastaByNameStartingWith("te")).thenReturn(copypastas);

      assertEquals(copypastas, copypastaService.findAllCopypastaStartsWith("te"));
      verify(copypastaRepository).findCopypastaByNameStartingWith("te");
      verifyNoMoreInteractions(copypastaRepository);
    }

    @Test
    void testFindByName_shouldPass() {
      Optional<Copypasta> copypasta = Optional.of(new Copypasta("test", "description", "message"));

      when(copypastaRepository.findCopypastaByName("test")).thenReturn(copypasta);

      assertEquals(copypasta, copypastaService.findCopypastaByName("test"));
      verify(copypastaRepository).findCopypastaByName("test");
			verifyNoMoreInteractions(copypastaRepository);
		}
	}

	@Nested
	class UpdateCopypasta {

		@Test
		void testUpdateName_shouldPass() {
			Copypasta original = new Copypasta("testname", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testname")).thenReturn(Optional.of(original));
      when(copypastaRepository.existsById("newname")).thenReturn(false);

			copypastaService.updateCopypasta("testname", CopypastaUpdateType.NAME, "newname");

			verify(copypastaRepository).findCopypastaByName("testname");
      verify(copypastaRepository).existsById("newname");
      verify(copypastaRepository).replaceCopypasta(argThat(name -> name.equals("testname")), argThat(copypasta ->
        copypasta.getName().equals("newname") &&
          copypasta.getDescription().equals("testDescription") &&
          copypasta.getMessage().equals("This is a message")));
      verifyNoMoreInteractions(copypastaRepository);
		}

		@Test
		void testUpdateNameToExistingPrimaryKey_shouldFail() {
			Copypasta original = new Copypasta("testname", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testname")).thenReturn(Optional.of(original));
      when(copypastaRepository.existsById("existingname")).thenReturn(true);

      assertThrows(IllegalStateException.class, () -> copypastaService.updateCopypasta("testname", CopypastaUpdateType.NAME, "existingname"));

			verify(copypastaRepository).findCopypastaByName("testname");
      verify(copypastaRepository).existsById("existingname");
      verify(copypastaRepository, never()).replaceCopypasta(any(String.class), any(Copypasta.class));
      verifyNoMoreInteractions(copypastaRepository);
		}

		@Test
		void testUpdateDescription_shouldPass() {
			Copypasta original = new Copypasta("testname", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testname")).thenReturn(Optional.of(original));

			copypastaService.updateCopypasta("testname", CopypastaUpdateType.DESCRIPTION, "newDescription");

			verify(copypastaRepository).findCopypastaByName("testname");
      verify(copypastaRepository).save(argThat(copypasta ->
        copypasta.getName().equals("testname") &&
          copypasta.getDescription().equals("newDescription") &&
          copypasta.getMessage().equals("This is a message")));
      verifyNoMoreInteractions(copypastaRepository);
		}

		@Test
		void testUpdateMessage_shouldPass() {
			Copypasta original = new Copypasta("testname", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testname")).thenReturn(Optional.of(original));

			copypastaService.updateCopypasta("testname", CopypastaUpdateType.MESSAGE, "newMessage");

			verify(copypastaRepository).findCopypastaByName("testname");
      verify(copypastaRepository).save(argThat(copypasta ->
        copypasta.getName().equals("testname") &&
          copypasta.getDescription().equals("testDescription") &&
          copypasta.getMessage().equals("newMessage")));
      verifyNoMoreInteractions(copypastaRepository);
		}

		@Test
    void testNullUpdate_shouldFail() {
      assertThrows(IllegalArgumentException.class, () -> copypastaService.updateCopypasta("testname", CopypastaUpdateType.NAME, null));

      verifyNoMoreInteractions(copypastaRepository);
		}

		@Test
    void testInvalidNameUpdate_shouldFail() {
			Copypasta original = new Copypasta("testname", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testname")).thenReturn(Optional.of(original));
      when(copypastaRepository.existsById("Invalid Name 1")).thenReturn(false);

      assertThrows(IllegalArgumentException.class, () -> copypastaService.updateCopypasta("testname", CopypastaUpdateType.NAME, "Invalid Name 1"));

			verify(copypastaRepository).findCopypastaByName("testname");
      verify(copypastaRepository).existsById("Invalid Name 1");
      verify(copypastaRepository, never()).replaceCopypasta(any(String.class), any(Copypasta.class));
      verifyNoMoreInteractions(copypastaRepository);
		}

		@Test
    void testInvalidDescriptionUpdate_shouldFail() {
			Copypasta original = new Copypasta("testname", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testname")).thenReturn(Optional.of(original));

      assertThrows(IllegalArgumentException.class, () -> copypastaService.updateCopypasta("testname", CopypastaUpdateType.DESCRIPTION, repeatString('b', 150)));

			verify(copypastaRepository).findCopypastaByName("testname");
      verify(copypastaRepository, never()).save(any(Copypasta.class));
      verifyNoMoreInteractions(copypastaRepository);
		}

		@Test
    void testInvalidMessageUpdate_shouldFail() {
			Copypasta original = new Copypasta("testname", "testDescription", "This is a message");

			when(copypastaRepository.findCopypastaByName("testname")).thenReturn(Optional.of(original));

      assertThrows(IllegalArgumentException.class, () -> copypastaService.updateCopypasta("testname", CopypastaUpdateType.MESSAGE, repeatString('c', 3000)));

			verify(copypastaRepository).findCopypastaByName("testname");
      verify(copypastaRepository, never()).save(any(Copypasta.class));
      verifyNoMoreInteractions(copypastaRepository);
		}

		@Test
		void testUpdateNameNameNotExists_shouldFail() {
      when(copypastaRepository.findCopypastaByName("notExists")).thenReturn(Optional.empty());

      assertThrows(NoSuchElementException.class, () -> copypastaService.updateCopypasta("notExists", CopypastaUpdateType.NAME, "newname"));

			verify(copypastaRepository).findCopypastaByName("notExists");
      verifyNoMoreInteractions(copypastaRepository);
		}
	}

	private String repeatString(char ch, int times) {
		StringBuilder builder = new StringBuilder();
		return builder.repeat(ch, times).toString();
	}
}
