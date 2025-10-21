package top100liked;

import java.util.ArrayList;
import java.util.List;

public class L_208 {

    public static void main(String[] args) {

    }
}


class Trie {

    Trie[] children;

    List<String> words;

    public Trie() {
        children = new Trie[26];
        words = new ArrayList<>();
    }


    public void insert(String word) {
        int len = word.length();
        Trie cur = this;
        for (int i = 0; i < len; i++) {
            char ch = word.charAt(i);
            if (cur.children[ch - 'a'] == null) {
                cur.children[ch - 'a'] = new Trie();
            }
            cur = cur.children[ch - 'a'];
        }
        cur.words.add(word);
    }

    public boolean search(String word) {
        int len = word.length();
        Trie cur = this;
        for (int i = 0; i < len; i++) {
            char ch = word.charAt(i);
            if (cur.children[ch - 'a'] == null) {
                return false;
            }
            cur = cur.children[ch - 'a'];
        }
        return cur.words.contains(word);
    }

    public boolean startsWith(String prefix) {
        int len = prefix.length();
        Trie cur = this;
        for (int i = 0; i < len; i++) {
            char ch = prefix.charAt(i);
            if (cur.children[ch - 'a'] == null) {
                return false;
            }
            cur = cur.children[ch - 'a'];
        }
        return true;
    }
}