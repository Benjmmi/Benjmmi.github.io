package leetcode;

public class L_208 {


    public static void main(String[] args) {
//        {
//            Trie trie = new Trie();
//            trie.insert("apple");
//            System.out.println(trie.search("apple"));   // 返回 True
//            System.out.println(trie.search("app"));     // 返回 False
//            System.out.println(trie.startsWith("app")); // 返回 True
//            trie.insert("app");
//            System.out.println(trie.search("app"));// 返回 True
//        }
        {
            Trie trie = new Trie();
            trie.insert("ab");
            System.out.println(trie.search("abc"));   // 返回 false
            System.out.println(trie.search("ab"));     // 返回 true
            System.out.println(trie.startsWith("abc"));     // 返回 false
            System.out.println(trie.startsWith("ab")); // 返回 true
            trie.insert("ab");
            System.out.println(trie.search("abc")); // false
            System.out.println(trie.startsWith("abc")); // false
            trie.insert("abc");
            System.out.println(trie.search("abc")); // true
            System.out.println(trie.startsWith("abc")); // true
        }
    }
}


class Trie {

    class BPlus {
        BPlus[] bplus;
        boolean isEmpty = true;
        boolean end = false;

        public BPlus() {
            this.bplus = new BPlus[LENGTH];
        }
    }

    int LENGTH = 26;
    BPlus bPlus;


    public Trie() {
        bPlus = new BPlus();
        bPlus.isEmpty = false;
    }

    public void insert(String word) {
        BPlus bPlus1 = this.bPlus;
        int length = word.toCharArray().length;
        for (int i = 0; i < length; i++) {
            char c = word.charAt(i);
            if (bPlus1.bplus[c - 'a'] == null) {
                bPlus1.bplus[c - 'a'] = new BPlus();
                if (i < length - 1) {
                    bPlus1.bplus[c - 'a'].isEmpty = false;
                }
            }
            bPlus1 = bPlus1.bplus[c - 'a'];
        }


        bPlus1.end = true;
    }

    public boolean search(String word) {
        BPlus bPlus1 = this.bPlus;
        for (char c : word.toCharArray()) {
            if (bPlus1.bplus[c - 'a'] != null) {
                bPlus1 = bPlus1.bplus[c - 'a'];
            } else {
                return false;
            }
        }
        return bPlus1.end;
    }

    public boolean startsWith(String prefix) {
        BPlus bPlus1 = this.bPlus;
        for (char c : prefix.toCharArray()) {
            if (bPlus1.bplus[c - 'a'] != null) {
                bPlus1 = bPlus1.bplus[c - 'a'];
            } else {
                return false;
            }
        }
        return true;
    }
}
