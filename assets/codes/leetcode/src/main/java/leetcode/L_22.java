package leetcode;

import java.util.ArrayList;
import java.util.List;

public class L_22 {
    public List<String> generateParenthesis(int n) {
        List<String> answer = new ArrayList<>();
        dfs(0, 0, new char[n*2], n, n * 2, answer);
        return answer;
    }

    public void dfs(int i, int open, char[] path, int n, int total, List<String> answer) {
        if (i == total) {
            answer.add(new String(path));
            return;
        }
        if (open < n) {
            path[i] = '(';
            dfs(i + 1, open + 1, path, n, total, answer);
        }
        if (i - open < open) {
            path[i] = ')';
            dfs(i + 1, open, path, n, total, answer);
        }
    }


    public static void main(String[] args) {
        L_22 l22 = new L_22();
        System.out.println(l22.generateParenthesis(3));
        System.out.println(l22.generateParenthesis(2));
        System.out.println(l22.generateParenthesis(1));
    }
}
