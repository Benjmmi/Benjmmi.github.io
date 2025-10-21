package top100liked;

import java.util.ArrayList;
import java.util.List;

public class L_22 {
    public List<String> generateParenthesis(int n) {
        List<String> res = new ArrayList<>();

        dfs(0, 0, "", res, n);
        return res;
    }

    public void dfs(int left, int right, String s, List<String> res, int n) {
        if (s.length() == n*2){
            res.add(s);
            return;
        }
        if (left < n){
            dfs(left+1, right, s+"(", res, n);
        }
        if (right < left && left+right < n*2){
            dfs(left, right+1, s+")", res, n);
        }
    }

    public static void main(String[] args) {
        L_22 l22 = new L_22();
        System.out.println(l22.generateParenthesis(3));
    }
}
