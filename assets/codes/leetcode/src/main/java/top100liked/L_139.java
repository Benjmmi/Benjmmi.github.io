package top100liked;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class L_139 {
    public boolean wordBreak(String s, List<String> wordDict) {
        boolean[] dp = new boolean[s.length() + 1];

        Set<Integer> wordSize = new HashSet<>();
        for (String string : wordDict) {
            wordSize.add(string.length());
        }
        dp[0] = true;
        for (int i = 0; i < dp.length; i++) {
            if (!dp[i]) {
                continue;
            }
            for (Integer item : wordSize) {
                if(i+item > dp.length) {
                    continue;
                }
                if (dp[i + item]){
                    continue;
                }
                dp[i + item] = wordDict.contains(s.substring(i, i + item));
            }
        }


        return dp[s.length()];


//        boolean[] dp = new boolean[s.length() + 1];
//
//        Queue<Integer> q = new PriorityQueue<>();
//
//        Set<Integer> wordSize = new HashSet<>();
//        for (String string : wordDict) {
//            wordSize.add(string.length());
//        }
//
//
//        q.offer(0);
//        char[] chars = s.toCharArray();
//
//        while (!q.isEmpty()) {
//            int size = q.size();
//            for (int i = 0; i < size; i++) {
//                int start = q.poll();
//                if (start >= s.length()) {
//                    break;
//                }
//                for (Integer item : wordSize) {
//                    if (start + item > s.length()) {
//                        break;
//                    }
//                    String b = s.substring(start, start + item);
//                    if (wordDict.contains(b)) {
//                        q.offer(start + item);
//                        dp[start + item] = true;
//                    }
//                }
//            }
//        }
//        return dp[s.length()];
    }


    public static void main(String[] args) {
        L_139 l139 = new L_139();
        System.out.println(l139.wordBreak("leetcode", Arrays.asList("leet", "code")));
    }
}
