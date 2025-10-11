package top100liked;

import java.util.*;

public class L_438 {
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> res = new ArrayList<>();
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < p.length(); i++) {
            char c = p.charAt(i);
            map.put(c, map.getOrDefault(c, 0) - 1);
        }
        for (int i = 0; i < s.length(); i++) {
            if (i >= p.length()) {
                char c = s.charAt(i - p.length());
                int val = map.getOrDefault(c, 0) - 1;
                map.put(c, val);
                if (val == 0) {
                    map.remove(c);
                }
            }
            int val = map.getOrDefault(s.charAt(i), 0) + 1;
            map.put(s.charAt(i), val);
            if (val == 0) {
                map.remove(s.charAt(i));
            }
            if (map.isEmpty()) {
                res.add(i - p.length() + 1);
            }
        }


        return res;
    }

    public static void main(String[] args) {
        L_438 l438 = new L_438();
        System.out.println(l438.findAnagrams("cbaebabacd", "abc"));
        System.out.println(l438.findAnagrams("abab", "ab"));
        System.out.println(l438.findAnagrams("a", "a"));
        System.out.println(l438.findAnagrams("a", "ab"));
        System.out.println(l438.findAnagrams("aaaaaaaaa", "a"));
        System.out.println(l438.findAnagrams("", ""));
    }
}
