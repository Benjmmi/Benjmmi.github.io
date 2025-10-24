package top100liked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class L_131 {
    public List<List<String>> partition(String s) {
        if (s.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<String>> result = new ArrayList<>();

        backtrack(s, 0, new ArrayList<>(), result);


        return result;
    }

    public void backtrack(String s, int startIndex, List<String> path, List<List<String>> result) {
        if (startIndex >= s.length()) {
            result.add(new ArrayList<>(path));
        }
        for (int i = startIndex; i < s.length(); i++) {
            String sub = s.substring(startIndex, i + 1);
            if (isPalindrome(sub)) {
                path.add(sub);
                backtrack(s, i + 1, path, result);
                path.remove(path.size() - 1);
            }
        }
    }

    public boolean isPalindrome(String s) {
        if (s.length() == 1) {
            return true;
        }
        for (int i = 0; i < s.length() / 2; i++) {
            if (s.charAt(i) != s.charAt(s.length() - i - 1)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        L_131 l131 = new L_131();
        System.out.println(l131.partition("aabb"));
//        System.out.println(l131.partition("aab"));
//        System.out.println(l131.partition("a"));
    }
}
