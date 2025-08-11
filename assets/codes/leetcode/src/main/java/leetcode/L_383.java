package leetcode;

import java.util.HashMap;
import java.util.Map;

public class L_383 {
    public boolean canConstruct(String ransomNote, String magazine) {
        if (magazine.length() < ransomNote.length()) {
            return false;
        }
        Map<Character, Integer> ransomNoteMap = new HashMap<>();
        Map<Character, Integer> magazineMap = new HashMap<>();
        for (int i = 0; i < magazine.length(); i++) {
            char m = magazine.charAt(i);
            magazineMap.put(m, magazineMap.getOrDefault(m, 0) + 1);
            if (i < ransomNote.length()) {
                char r = ransomNote.charAt(i);
                ransomNoteMap.put(r, ransomNoteMap.getOrDefault(r, 0) + 1);
            }
        }

        for (Map.Entry<Character, Integer> item : ransomNoteMap.entrySet()) {
            int value = item.getValue();
            Integer cmp = magazineMap.get(item.getKey());
            if (cmp == null || cmp < value) {
                return false;
            }
        }


        return true;
    }

    public static void main(String[] args) {
        L_383 l383 = new L_383();
        System.out.println(l383.canConstruct("a", "b"));
        System.out.println(l383.canConstruct("aa", "ab"));
        System.out.println(l383.canConstruct("aa", "aab"));
        System.out.println(l383.canConstruct("aab", "baa"));
    }
}
