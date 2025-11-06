package top100liked;

import java.util.Stack;

public class L_32 {
    public int longestValidParentheses(String s) {
        int total = 0;

        char[] chars = s.toCharArray();


        boolean[] dp = new boolean[s.length()];

        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            if (aChar == '(') {
                stack.push(i);
            }
            if (aChar == ')' && !stack.isEmpty()) {
                dp[i] = true;
                dp[stack.pop()] = true;
            }
        }
        int sum = 0;
        for (int i = 0; i < dp.length; i++) {
            if (dp[i]) {
                sum++;
            } else {
                total = Math.max(total, sum);
                sum = 0;
            }
        }
        total = Math.max(total, sum);

        return total;

    }

    public static void main(String[] args) {
        L_32 l32 = new L_32();
        l32.longestValidParentheses("(()");
    }
}
