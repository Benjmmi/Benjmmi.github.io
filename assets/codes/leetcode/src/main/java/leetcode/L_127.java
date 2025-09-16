package leetcode;

import java.util.*;

public class L_127 {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        if (beginWord.equals(endWord)) {
            return 0;
        }
        Set<String> wordSet = new HashSet<>();

        for (String s : wordList) {
            wordSet.add(s);
        }
        Queue<String> queue = new ArrayDeque<>();
        queue.offer(beginWord);

        for (int step = 1; !queue.isEmpty(); step++) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String curr = queue.poll();
                Iterator<String> iterator = wordSet.iterator();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    if (diff(curr, next) == 1) {
                        if (next.equals(endWord)) {
                            return step+1;
                        }
                        queue.offer(next);
                        iterator.remove();
                    }
                }
            }
        }

        return 0;
    }

    public int diff(String beginWord, String compareWord) {
        int diff = 0;
        for (int i = 0; i < beginWord.length(); i++) {
            if (beginWord.charAt(i) != compareWord.charAt(i)) {
                diff++;
            }
        }
        return diff;
    }


    public static void main(String[] args) {
        L_127 l127 = new L_127();
        {
            System.out.println(l127.ladderLength("hit", "cog", Arrays.asList("hot", "dot", "dog", "lot", "log", "cog")));
        }
    }
}
