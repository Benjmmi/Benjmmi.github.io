package top100liked;

import java.util.ArrayList;
import java.util.List;

public class L_17 {
    String[][] numbers = new String[][]{
            {}, {"a", "b", "c"}, {"d", "e", "f"}
            , {"g", "h", "i"}, {"j", "k", "l"}, {"m", "n", "o"}
            , {"p", "q", "r", "s"}, {"t", "u", "v"}, {"w", "x", "y", "z"}
    };

    public List<String> letterCombinations(String digits) {
        List<String> res = new ArrayList<>();
        if (digits == null || digits.length() == 0) return res;
        dfs(digits,"",0,res);
        return res;
    }

    public void dfs(String digits, String item, int index, List<String> res) {
        if (index == digits.length()) {
            res.add(item);
            return;
        }
        for (String s : numbers[digits.charAt(index)-'1']) {
            dfs(digits, item + s, index+1, res);
        }
    }

    public static void main(String[] args) {
        L_17 l17 =  new L_17();
        System.out.println(l17.letterCombinations("23"));
    }
}
