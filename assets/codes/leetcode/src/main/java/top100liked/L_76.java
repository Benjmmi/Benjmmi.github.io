package top100liked;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class L_76 {
    public String minWindow(String s, String t) {
        if (s.equals(t)) {
            return s;
        }
        if (s.length() < t.length()) {
            return "";
        }
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < t.length(); i++) {
            map.put(t.charAt(i), map.getOrDefault(t.charAt(i), 0) + 1);
        }
        Map<Character, Integer> counter = new HashMap<>();

        int start = 0;
        int minLength = Integer.MAX_VALUE;
        int left = 0;
        int right = 0;
        int valid = 0;
        for (; right < s.length(); right++) {
            char c = s.charAt(right);
            counter.put(c, counter.getOrDefault(c, 0) + 1);
            if (map.containsKey(c) && Objects.equals(counter.get(c), map.get(c))) {
                valid++;
            }
            while (valid == map.size()) {
                if (right - left + 1 < minLength) {
                    start = left;
                    minLength = right - left + 1;
                }

                c = s.charAt(left);
                left++;
                if (map.containsKey(c)) {
                    counter.put(c, counter.get(c) - 1);
                    if (counter.get(c) < map.get(c)) {
                        valid--;
                        break;
                    }
                }

            }
        }


        return minLength == Integer.MAX_VALUE ? "" : s.substring(start, start + minLength);

    }

    public static void main(String[] args) {

    }
}
