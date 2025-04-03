package textconverter;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class TextConverterTest {

	@Test
	void testConvertToXml() throws IOException {
		String input = "Mary had a little lamb. Cinderella likes shoes.";
		StringReader reader = new StringReader(input);
		StringWriter writer = new StringWriter();

		TextConverter converter = new TextConverter();
		converter.convert(reader, writer, "xml");

		String output = writer.toString();

		assertTrue(output.startsWith("<?xml"), "XML should start with declaration");
		assertTrue(output.contains("<text>"), "Should contain <text> element");

		assertTrue(output.contains("<word>a</word>"), "Should contain word 'a'");
		assertTrue(output.contains("<word>had</word>"), "Should contain word 'had'");
		assertTrue(output.contains("<word>lamb</word>"), "Should contain word 'lamb'");
		assertTrue(output.contains("<word>little</word>"), "Should contain word 'little'");
		assertTrue(output.contains("<word>Mary</word>"), "Should contain word 'Mary'");

		assertTrue(output.contains("<word>Cinderella</word>"), "Should contain word 'Cinderella'");
		assertTrue(output.contains("<word>likes</word>"), "Should contain word 'likes'");
		assertTrue(output.contains("<word>shoes</word>"), "Should contain word 'shoes'");

		int posA = output.indexOf("<word>a</word>");
		int posHad = output.indexOf("<word>had</word>");
		int posLamb = output.indexOf("<word>lamb</word>");
		int posLittle = output.indexOf("<word>little</word>");
		int posMary = output.indexOf("<word>Mary</word>");

		assertTrue(posA < posHad, "Word 'a' should appear before 'had'");
		assertTrue(posHad < posLamb, "Word 'had' should appear before 'lamb'");
		assertTrue(posLamb < posLittle, "Word 'lamb' should appear before 'little'");
		assertTrue(posLittle < posMary, "Word 'little' should appear before 'Mary'");
	}

	@Test
	void testConvertToCsv() throws IOException {
		String input = "Mary had a little lamb. Cinderella likes shoes.";
		StringReader reader = new StringReader(input);
		StringWriter writer = new StringWriter();

		TextConverter converter = new TextConverter();
		converter.convert(reader, writer, "csv");

		String output = writer.toString().trim();
		String[] lines = output.split("\n");

		assertEquals("Sentence #, Word 1, Word 2, Word 3, Word 4, Word 5", lines[0].trim());

		assertTrue(lines[1].contains("1, a, had, lamb, little, Mary"), "First sentence should be properly formatted");

		assertTrue(lines[2].contains("2, Cinderella, likes, shoes"), "Second sentence should be properly formatted");

		assertTrue(output.contains("a"), "Should contain word 'a'");
		assertTrue(output.contains("had"), "Should contain word 'had'");
		assertTrue(output.contains("lamb"), "Should contain word 'lamb'");
		assertTrue(output.contains("little"), "Should contain word 'little'");
		assertTrue(output.contains("Mary"), "Should contain word 'Mary'");
		assertTrue(output.contains("Cinderella"), "Should contain word 'Cinderella'");
		assertTrue(output.contains("likes"), "Should contain word 'likes'");
		assertTrue(output.contains("shoes"), "Should contain word 'shoes'");
	}

	@Test
	void testUnsupportedFormat() {
		String input = "Mary had a little lamb.";
		StringReader reader = new StringReader(input);
		StringWriter writer = new StringWriter();

		TextConverter converter = new TextConverter();

		assertThrows(IllegalArgumentException.class, () ->
				converter.convert(reader, writer, "json"),
			"Should throw exception for unsupported format"
		);
	}

	@Test
	void testEmptyInput() throws IOException {
		String input = "";
		StringReader reader = new StringReader(input);
		StringWriter writer = new StringWriter();

		TextConverter converter = new TextConverter();
		converter.convert(reader, writer, "xml");

		String output = writer.toString().trim();

		assertTrue(output.contains("<text>"), "Should contain root element");
		assertTrue(output.contains("</text>"), "Should contain closing root element");
		assertFalse(output.contains("<sentence>"), "Should not contain any sentences");
	}

	@Test
	void testWhitespaceHandling() throws IOException {
		String input = "  Mary   had a little  lamb  .  Peter   called for the wolf   ,  and Aesop came.";
		StringReader reader = new StringReader(input);
		StringWriter writer = new StringWriter();

		TextConverter converter = new TextConverter();
		converter.convert(reader, writer, "xml");

		String output = writer.toString();

		assertTrue(output.contains("<word>a</word>"), "Should contain word 'a'");
		assertTrue(output.contains("<word>had</word>"), "Should contain word 'had'");
	}
}