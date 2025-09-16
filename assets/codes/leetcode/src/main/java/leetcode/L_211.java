package leetcode;

import java.util.ArrayDeque;
import java.util.Queue;

public class L_211 {
    public static void main(String[] args) {
        WordDictionary wordDictionary = new WordDictionary();
        wordDictionary.addWord("bad");
        wordDictionary.addWord("dad");
        wordDictionary.addWord("mad");
        System.out.println(wordDictionary.search("pad")); // 返回 False
        System.out.println(wordDictionary.search("bad")); // 返回 True
        System.out.println(wordDictionary.search(".ad")); // 返回 True
        System.out.println(wordDictionary.search("b..")); // 返回 True
    }
}

class WordDictionary {
    WordDictionary[] dictionaries;
    boolean isEnd;
    int LENGTH = 26;

    public WordDictionary() {
        dictionaries = new WordDictionary[LENGTH];
    }

    public void addWord(String word) {
        WordDictionary temp = this;
        for (int i = 0; i < word.length(); i++) {
            int c = word.charAt(i) - 'a';
            if (temp.dictionaries[c] == null) {
                temp.dictionaries[c] = new WordDictionary();
            }
            temp = temp.dictionaries[c];
        }
        temp.isEnd = true;
    }

    public boolean search(String word) {

        Queue<WordDictionary> queue = new ArrayDeque<>();
        queue.add(this);
        int index = 0;

        while (!queue.isEmpty() && index < word.length()) {
            int size = queue.size();
            char c = word.charAt(index);
            for (int j = 0; j < size; j++) {
                WordDictionary wordDictionary = queue.poll();
                assert wordDictionary != null;
                if (c == '.') {
                    for (WordDictionary dictionary : wordDictionary.dictionaries) {
                        if (index == word.length() - 1 && dictionary != null && dictionary.isEnd) {
                            return true;
                        }
                        if (dictionary != null) {
                            queue.offer(dictionary);
                        }
                    }
                } else {
                    if (wordDictionary.dictionaries[c - 'a'] != null) {
                        if (index == word.length() - 1 && wordDictionary.dictionaries[c - 'a'].isEnd) {
                            return true;
                        }
                        queue.offer(wordDictionary.dictionaries[c - 'a']);
                    }
                }
            }
            index++;
        }
        return false;
    }
}
