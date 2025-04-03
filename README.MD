# Text Converter

A Java application that converts text into XML or CSV format. The tool parses input text, breaks it into sentences and words, and outputs the structured data in the requested format with alphabetically sorted words.

## Features

- Parses text from standard input
- Breaks text into sentences and words
- Sorts words alphabetically (case-insensitive)
- Outputs in XML or CSV format
- Handles Unicode characters, whitespace, and punctuation
- Designed for efficient memory usage even with large inputs

## Requirements

- Java 11 or higher
- Maven for building

## Building

To build the application:

```bash
mvn clean package
```

This will create an executable JAR file with dependencies in the `target` directory.

## Usage

The application reads from standard input and writes to standard output. You need to specify the output format as a command-line argument.

### Basic Usage

```bash
java -jar target/text-converter-1.0-SNAPSHOT-jar-with-dependencies.jar <format>
```

Where `<format>` is either `xml` or `csv`.

### Input/Output Redirection

You can use standard input/output redirection to process files:

```bash
# Convert input.txt to XML format
cat input.txt | java -jar target/text-converter-1.0-SNAPSHOT-jar-with-dependencies.jar xml > output.xml

# Convert input.txt to CSV format
cat input.txt | java -jar target/text-converter-1.0-SNAPSHOT-jar-with-dependencies.jar csv > output.csv
```

### Memory Constraints

For processing very large files with limited memory, you can use the JVM argument `-Xmx`:

```bash
cat large.in | java -Xmx32m -jar target/text-converter-1.0-SNAPSHOT-jar-with-dependencies.jar xml > output.xml
```

## Output Formats

### XML Format

The application outputs XML in the following structure:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<text>
    <sentence>
        <word>alphabetically</word>
        <word>are</word>
        <word>sorted</word>
        <word>Words</word>
    </sentence>
    <sentence>
        <word>another</word>
        <word>Here's</word>
        <word>sentence</word>
    </sentence>
</text>
```

### CSV Format

The application outputs CSV in the following structure:

```
, Word 1, Word 2, Word 3, Word 4
Sentence 1, alphabetically, are, sorted, Words
Sentence 2, another, Here's, sentence
```

## Code Structure

- `TextConverter`: Main application class that handles application flow
- `TextParser`: Parses input text into sentences and words
- `Formatter`: Interface for output formatting
  - `XmlFormatter`: Formats output as XML
  - `CsvFormatter`: Formats output as CSV
- `Sentence`: Model class representing a sentence with words

## Implementation Notes

- The application uses regular expressions to identify sentences and words
- Words within sentences are sorted alphabetically (case-insensitive)
- Streaming operations are used for efficient memory usage
- The architecture follows SOLID principles for maintainability and extensibility
- No external dependencies are used for the core functionality, only Lombok for boilerplate reduction

## Limitations

- The sentence detection is based on standard punctuation (., !, ?) and may not handle all edge cases
- Very large inputs are processed sequentially, with memory constraints controlled by JVM settings
- Currently, only XML and CSV output formats are supported, but the architecture allows for easy extension

## Testing

Unit tests are provided to validate the functionality:

```bash
mvn test
```

The test suite covers:
- Basic text conversion to XML and CSV
- Handling of empty or whitespace-heavy input
- Error handling for unsupported formats