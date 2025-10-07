package leetcode;

import java.util.*;

public class L_139 {
    int max = 0;

    public boolean wordBreak(String s, List<String> wordDict) {
        List<String> list = new ArrayList<>(wordDict);
        wordDict = new ArrayList<>(list);

        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String word = iterator.next();
            wordDict.remove(word);
            boolean b = find(word, wordDict, 0);
            if (b) {
                iterator.remove();
            } else {
                wordDict.add(word);
            }
        }

        return find3(s, wordDict);
    }

    public boolean find(String s, List<String> wordDict, int dp) {
        if (dp == s.length()) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = dp; i < s.length(); i++) {
            sb.append(s.charAt(i));
            if (max != 0 && sb.length() > max) {
                return false;
            }
            if (wordDict.contains(sb.toString())) {
                if (find(s, wordDict, i + 1)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean find3(String s, List<String> wordDict) {
        boolean[] dp = new boolean[s.length() + 1];

        Set<Integer> set = new HashSet<>();
        for (String string : wordDict) {
            set.add(string.length());
        }

        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(0);
        dp[0] = true;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int cur = queue.poll();
                for (Integer integer : set) {
                    if (cur + integer > s.length()) {
                        continue;
                    }
                    String word = s.substring(cur, cur + integer);
                    if (wordDict.contains(word) && !dp[cur + integer]) {
                        dp[cur + integer] = true;
                        queue.add(cur + integer);
                    }
                }
            }
        }
        return dp[s.length()];
    }

    public static void main(String[] args) {
        L_139 l139 = new L_139();
        System.out.println(l139.wordBreak("leetcode", Arrays.asList("leet", "code")));
        System.out.println(l139.wordBreak("applepenapple", Arrays.asList("apple", "pen")));
        System.out.println(l139.wordBreak("catsandog", Arrays.asList("cats", "dog", "sand", "and", "cat")));
        System.out.println(l139.wordBreak("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab", Arrays.asList("a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", "aaaaaaa", "aaaaaaaa", "aaaaaaaaa", "aaaaaaaaaa")));
        System.out.println(l139.wordBreak("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabaabaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", Arrays.asList("aa", "aaa", "aaaa", "aaaaa", "aaaaaa", "aaaaaaa", "aaaaaaaa", "aaaaaaaaa", "aaaaaaaaaa", "ba")));
    }
}
