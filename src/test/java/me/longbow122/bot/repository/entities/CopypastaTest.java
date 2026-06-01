package me.longbow122.bot.repository.entities;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CopypastaTest {

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

  private static Stream<Arguments> invalidCopypastas() {
    return Stream.of(
      Arguments.of(repeatString('a', 50), repeatString('b', 150), repeatString('c', 3000)),
      Arguments.of("", "", ""),
      Arguments.of(null, null, null),
      Arguments.of("", "description", "this is a message"),
      Arguments.of(repeatString('a', 50), "description", "this is a message"),
      Arguments.of(null, "description", "this is a message"),
      Arguments.of("name", "", "this is a message"),
      Arguments.of("name", repeatString('b', 150), "this is a message"),
      Arguments.of("name", null, "this is a message"),
      Arguments.of("name", "description", ""),
      Arguments.of("name", "description", repeatString('c', 3000)),
      Arguments.of("name", "description", null),
      Arguments.of(" ", " ", " "),
      Arguments.of(" ", "description", "this is a message"),
      Arguments.of("name", " ", "this is a message"),
      Arguments.of("name", "description", " "),
      Arguments.of("name123", "description", "This is a message"),
      Arguments.of("name!!!", "description", "This is a message"),
      Arguments.of("nameABC", "description", "This is a message")
    );
  }

  private static String repeatString(char ch, int times) {
    StringBuilder builder = new StringBuilder();
    return builder.repeat(ch, times).toString();
  }

  @Nested
  class CreateCopypasta {

    @Test
    void testValidConstruction_shouldPass() {
      // ? Test that constructing a record with the right information formatting works
      Copypasta pasta = new Copypasta("name", "testDescription", "This is a message");
      assertEquals("name", pasta.getName());
      assertEquals("testDescription", pasta.getDescription());
      assertEquals("This is a message", pasta.getMessage());
    }

    @ParameterizedTest
    @MethodSource("me.longbow122.bot.repository.entities.CopypastaTest#invalidCopypastas")
    void testInvalidConstruction_shouldFail(String name, String description, String message) {
      // ? Test that constructing a Copypasta that breaks model constraints fails
      assertThrows(IllegalArgumentException.class, () -> new Copypasta(name, description, message));
    }
  }

  @Nested
  class UpdateCopypasta {

    @Test
    void testValidUpdate_shouldPass() {
      Copypasta pasta = new Copypasta("name", "testDescription", "This is a message");

      pasta.setName("newname");
      pasta.setDescription("newDescription");
      pasta.setMessage("This is a new message");

      assertEquals("newname", pasta.getName());
      assertEquals("newDescription", pasta.getDescription());
      assertEquals("This is a new message", pasta.getMessage());
    }

    @Test
    void testInvalidNameUpdate_shouldFail() {
      Copypasta pasta = new Copypasta("name", "description", "This is a message");

      assertThrows(IllegalArgumentException.class, () -> pasta.setName("name123"));

      assertEquals("name", pasta.getName());
    }

    @Test
    void testInvalidDescriptionUpdate_shouldFail() {
      Copypasta pasta = new Copypasta("name", "description", "This is a message");

      assertThrows(IllegalArgumentException.class, () -> pasta.setDescription(""));

      assertEquals("description", pasta.getDescription());
    }

    @Test
    void testInvalidMessageUpdate_shouldFail() {
      Copypasta pasta = new Copypasta("name", "description", "This is a message");

      assertThrows(IllegalArgumentException.class, () -> pasta.setMessage(" "));

      assertEquals("This is a message", pasta.getMessage());
    }
  }
}
