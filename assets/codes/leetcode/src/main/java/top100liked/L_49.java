package top100liked;

import java.util.*;

public class L_49 {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String,List<String>> map = new HashMap<>();
        for (String str : strs) {
            String[] strings = str.split("");
            Arrays.sort(strings);
            String s = String.join("", strings);
            map.computeIfAbsent(s, k -> new ArrayList<>()).add(str);
        }
        List<List<String>>  lists = new ArrayList<>(map.values());
        return lists;
    }

    public static void main(String[] args) {
        L_49 l49 = new L_49();
    }
}
