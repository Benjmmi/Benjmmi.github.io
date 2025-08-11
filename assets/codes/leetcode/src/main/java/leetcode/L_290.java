package leetcode;

import java.util.HashMap;
import java.util.Map;

public class L_290 {
    public boolean wordPattern(String pattern, String s) {
        String[] sa = s.split(" ");
        char[] cs = pattern.toCharArray();
        if (cs.length != sa.length) {
            return false;
        }

        Map<Character, String> ps = new HashMap<>();
        Map<String, Character> sp = new HashMap<>();
        for (int i = 0; i < cs.length; i++) {
            char c = cs[i];
            String p = sa[i];
            if (ps.get(c) == null) {
                ps.put(c, p);
            } else if (!ps.get(c).equals(p)) {
                return false;
            }
            if (sp.get(p) == null) {
                sp.put(p, c);
            } else if (sp.get(p) != c) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        L_290 l290 = new L_290();
        {
            System.out.println(l290.wordPattern("abba", "dog cat cat dog"));
        }
        {
            System.out.println(l290.wordPattern("abba", "dog cat cat fish"));
        }
        {
            System.out.println(l290.wordPattern("aaaa", "dog cat cat dog"));
        }
        {
            System.out.println(l290.wordPattern("abbc", "dog cat cat doo"));
        }
    }
}
