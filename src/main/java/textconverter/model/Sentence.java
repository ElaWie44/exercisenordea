package textconverter.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class Sentence {
    @Getter
    private final int index;
    private final List<String> words;

    public Sentence(int index) {
        this.index = index;
        this.words = new ArrayList<>(20);
    }

    public void addWord(String word) {
        if (word != null && !word.trim().isEmpty()) {
            words.add(word);
        }
    }

    public List<String> getWords() {
        words.sort(String.CASE_INSENSITIVE_ORDER);
        return words;
    }

}