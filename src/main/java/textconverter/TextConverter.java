package textconverter;

import textconverter.model.Sentence;
import textconverter.stream.StreamingParser;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TextConverter {
    private static final int MAX_MEMORY_WORDS = 10000; // Safety limit

    public void convert(Reader reader, Writer writer, String format) throws IOException {
        if (!format.equalsIgnoreCase("xml") && !format.equalsIgnoreCase("csv")) {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }

        Path tempFile = Files.createTempFile("text-converter-", ".tmp");
        try {
            try (BufferedWriter tempWriter = Files.newBufferedWriter(tempFile)) {
                char[] buffer = new char[8192];
                int read;
                while ((read = reader.read(buffer)) != -1) {
                    tempWriter.write(buffer, 0, read);
                }
            }

            try (PrintWriter printWriter = new PrintWriter(writer, true)) {
                if (format.equalsIgnoreCase("xml")) {
                    convertToXml(tempFile, printWriter);
                } else {
                    convertToCsv(tempFile, printWriter);
                }
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private void convertToXml(Path inputFile, PrintWriter writer) throws IOException {
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<text>");

        try (StreamingParser parser = new StreamingParser(Files.newBufferedReader(inputFile))) {
            Sentence sentence;
            while ((sentence = parser.nextSentence()) != null) {
                writer.println("  <sentence>");
                for (String word : sentence.getWords()) {
                    writer.println("    <word>" + escapeXml(word) + "</word>");
                }
                writer.println("  </sentence>");
            }
        }
        writer.println("</text>");
    }

    private void convertToCsv(Path inputFile, PrintWriter writer) throws IOException {
        int maxWords = 0;
        try (StreamingParser parser = new StreamingParser(Files.newBufferedReader(inputFile))) {
            Sentence sentence;
            while ((sentence = parser.nextSentence()) != null) {
                maxWords = Math.max(maxWords, sentence.getWords().size());
                if (maxWords > MAX_MEMORY_WORDS) {
                    throw new IOException("Sentence too large - exceeds word limit");
                }
            }
        }

        try (StreamingParser parser = new StreamingParser(Files.newBufferedReader(inputFile))) {
            writeCsvHeader(writer, maxWords);

            Sentence sentence;
            while ((sentence = parser.nextSentence()) != null) {
                writeCsvRow(writer, sentence, maxWords);
            }
        }
    }

    private void writeCsvHeader(PrintWriter writer, int maxWords) {
        writer.print("Sentence #");
        for (int i = 1; i <= maxWords; i++) {
            writer.print(", Word " + i);
        }
        writer.println();
    }

    private void writeCsvRow(PrintWriter writer, Sentence sentence, int maxWords) {
        writer.print(sentence.getIndex());
        List<String> words = sentence.getWords();
        for (int i = 0; i < maxWords; i++) {
            writer.print(",");
            if (i < words.size()) {
                writer.print(" " + words.get(i));
            }
        }
        writer.println();
    }

    private String escapeXml(String text) {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: java TextConverter <format> (xml|csv)");
        }

        new TextConverter().convert(
            new InputStreamReader(System.in),
            new OutputStreamWriter(System.out),
            args[0]
        );
    }
}