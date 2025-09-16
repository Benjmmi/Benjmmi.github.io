package leetcode;

import java.util.ArrayList;
import java.util.List;

public class L_17 {
    String[][] numbers = new String[][]{
            {}, {"a", "b", "c"}, {"d", "e", "f"},
            {"g", "h", "i"}, {"j", "k", "l"}, {"m", "n", "o"},
            {"p", "q", "r", "s"}, {"t", "u", "v"}, {"w", "x", "y", "z"}
    };

    public List<String> letterCombinations(String digits) {
        int n = digits.length();
        List<String> list = new ArrayList<>();

        dfs(digits, 0, "", list);

        return list;
    }

    public void dfs(String digits, int i, String word, List<String> list) {
        if (i >= digits.length()) {
            return;
        }
        int c = digits.charAt(i) - '1';
        String[] chars = numbers[c];
        for (String aChar : chars) {
            if (i == digits.length() - 1) {
                list.add(word + aChar);
            }
            dfs(digits, i + 1, word + aChar, list);
        }
    }

    public static void main(String[] args) {
        L_17 l17 = new L_17();

        System.out.println(l17.letterCombinations("23"));
        System.out.println(l17.letterCombinations("2"));

    }
}
