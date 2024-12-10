package me.longbow122.bot.dto.mapper;

import me.longbow122.datamodel.repository.entities.Copypasta;
import me.longbow122.bot.dto.CopypastaDTO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {CopypastaMapper.class})
public class CopypastaMapperTest {

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

	@MockBean
	private CopypastaMapper copypastaMapper;

	@Nested
	class ToDTO {

		@Test
		void testValidMapping_shouldPass() {
			// ? Test that mapping a record with the right information formatting works
			Copypasta pasta = new Copypasta("name", "testDescription", "This is a message");
			CopypastaDTO dto = CopypastaMapper.toDTO(pasta);
			assertEquals("name", dto.name());
			assertEquals("testDescription", dto.description());
			assertEquals("This is a message", dto.message());
		}

		@Test
		void testAllLargeSizeMapping_shouldFail() {
			// ? Test that mapping a Copypasta that breaks size constraints does not work with the constraints
			Copypasta pasta = new Copypasta(repeatString('a', 50), repeatString('b', 150), repeatString('c', 3000));
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testAllEmptyInsertion_shouldFail() {
			// ? Test that mapping a Copypasta that has all fields empty does not work with the constraints
			Copypasta pasta = new Copypasta("", "", "");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testAllNullMapping_shouldFail() {
			// ? Test that mapping a Copypasta that has all fields null does not work with the constraints
			Copypasta pasta = new Copypasta(null, null, null);
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testEmptyNameMapping_shouldFail() {
			// ? Test that mapping a Copypasta with an empty name does not work with the constraints
			Copypasta pasta = new Copypasta("", "description", "this is a message");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testLargeNameMapping_shouldFail() {
			// ? Test that mapping a Copypasta with a name that is too large does not work with the constraints
			Copypasta pasta = new Copypasta(repeatString('a', 50), "description", "this is a message");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testNullNameMapping_shouldFail() {
			// ? Test that mapping a Copypasta with a null name does not work with the constraints
			Copypasta pasta = new Copypasta(null, "description", "this is a message");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testEmptyDescriptionMapping_shouldFail() {
			// ? Test that mapping a Copypasta with an empty description does not work with the constraints
			Copypasta pasta = new Copypasta("name", "", "this is a message");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testLargeDescriptionMapping_shouldFail() {
			// ? Test that mapping a Copypasta with a description that is too large does not work with the constraints
			Copypasta pasta = new Copypasta("name", repeatString('b', 150), "this is a message");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testNullDescriptionMapping_shouldFail() {
			// ? Test that mapping a Copypasta with a null description does not work with the constraints
			Copypasta pasta = new Copypasta("name", null, "this is a message");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testEmptyMessageMapping_shouldFail() {
			// ? Test that mapping a Copypasta with a message that is empty does not work with the constraints
			Copypasta pasta = new Copypasta("name", "description", "");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testLargeMessageMapping_shouldFail() {
			// ? Test that mapping a Copypasta with a message that is too large does not work with the constraints
			Copypasta pasta = new Copypasta("name", "description", repeatString('c', 3000));
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testNullMessageMapping_shouldFail() {
			// ? Test that mapping a Copypasta with a null message does not work with the constraints
			Copypasta pasta = new Copypasta("name", "description", null);
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testAllWhitespaceOnlyMapping_shouldFail() {
			// ? Test that mapping a Copypasta with whitespace-only for all fields does not work with the constraints
			Copypasta pasta = new Copypasta(" ", " ", " ");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testWhitespaceOnlyNameMapping_shouldFail() {
			// ? Test that mapping a Copypasts with a whitespace-only name does not work with the constraints
			Copypasta pasta = new Copypasta(" ", "description", "this is a message");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testWhitespaceOnlyDescriptionMapping_shouldFail() {
			// ? Test that mapping a Copypasta with a whitespace-only description does not work with the constraints
			Copypasta pasta = new Copypasta("name", " ", "this is a message");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testWhitespaceOnlyMessageMapping_shouldFail() {
			// ? Test that mapping a Copypasta with a whitespace-only message does not work with the constraints
			Copypasta pasta = new Copypasta("name", "description", " ");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testContainsNumberNameMapping_shouldFail() {
			// ? Test that mapping a Copypasta with a number in the name does not work with the constraints.
			Copypasta pasta = new Copypasta("name123", "description", "This is a message");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testContainsSpecialCharacterNameMapping_shouldFail() {
			// ? Test that mapping a Copypasta with a special character in the name does not work with the constraints.
			Copypasta pasta = new Copypasta("name!!!", "description", "This is a message");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}

		@Test
		void testContainsUpperCharacterNameMapping_shouldFail() {
			// ? Test that mapping a Copypasta with an uppercase character in the name does not work with the constraints.
			Copypasta pasta = new Copypasta("nameABC", "description", "This is a message");
			assertThrows(IllegalArgumentException.class, () -> CopypastaMapper.toDTO(pasta));
		}
	}

	private String repeatString(char ch, int times) {
		StringBuilder builder = new StringBuilder();
		return builder.repeat(ch, times).toString();
	}
}
