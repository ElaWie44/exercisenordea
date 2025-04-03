package textconverter.stream;

import textconverter.model.Sentence;
import java.io.*;

public class StreamingParser implements AutoCloseable {
    private static final char[] SENTENCE_TERMINATORS = {'.', '!', '?'};
    private static final int MAX_WORD_LENGTH = 100;

    private final BufferedReader reader;
    private int sentenceIndex = 1;
    private final StringBuilder currentWord = new StringBuilder(MAX_WORD_LENGTH);
    private boolean endOfStream = false;
    private Sentence currentSentence;

    public StreamingParser(Reader reader) {
        this.reader = new BufferedReader(reader, 8192);
    }

    public Sentence nextSentence() throws IOException {
        if (endOfStream && (currentSentence == null || currentSentence.getWords().isEmpty())) {
            return null;
        }

        currentSentence = new Sentence(sentenceIndex++);

        readUntilSentenceComplete();

        return currentSentence.getWords().isEmpty() ? null : currentSentence;
    }

    private void readUntilSentenceComplete() throws IOException {
        int charCode;

        while ((charCode = reader.read()) != -1) {
            char ch = (char) charCode;
            boolean isWordChar = isWordCharacter(ch);

            if (isWordChar) {
                addToCurrentWord(ch);
            } else {
                finishCurrentWord();

                if (isSentenceTerminator(ch) && !currentSentence.getWords().isEmpty()) {
                    return;
                }
            }
        }

        endOfStream = true;
        finishCurrentWord();
    }

    private void addToCurrentWord(char ch) {
        if (currentWord.length() < MAX_WORD_LENGTH) {
            currentWord.append(ch);
        }
    }

    private void finishCurrentWord() {
        if (currentWord.length() == 0) {
            return;
        }

        String word = currentWord.toString().trim();
        if (!word.isEmpty()) {
            currentSentence.addWord(removePunctuation(word));
        }
        currentWord.setLength(0);
    }

    private String removePunctuation(String word) {
        if (word.isEmpty()) {
            return word;
        }

        char lastChar = word.charAt(word.length() - 1);
        if (isPunctuationMark(lastChar)) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }

    private boolean isWordCharacter(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '-' || ch == '\'';
    }

    private boolean isSentenceTerminator(char ch) {
        for (char terminator : SENTENCE_TERMINATORS) {
            if (ch == terminator) {
                return true;
            }
        }
        return false;
    }

    private boolean isPunctuationMark(char ch) {
        return ch == '.' || ch == ',' || ch == '!' || ch == '?';
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}