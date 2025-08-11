package leetcode;

import java.util.*;
import java.util.stream.Collectors;

public class L_49 {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> result = new HashMap<>();

        for (String str : strs) {
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            List<String> value = result.get(key);
            if (value == null){
                value = new ArrayList<>();
                value.add(str);
                result.put(key,value);
            } else {
                value.add(str);
            }
        }
        return new ArrayList<>(result.values());
    }

    public static void main(String[] args) {
        L_49 l49 = new L_49();
        {
            System.out.println(l49.groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}));
        }
        {
            System.out.println(l49.groupAnagrams(new String[]{""}));
        }
        {
            System.out.println(l49.groupAnagrams(new String[]{"a"}));
        }
    }
}
