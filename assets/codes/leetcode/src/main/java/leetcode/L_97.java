package leetcode;

import java.util.HashSet;
import java.util.Set;

public class L_97 {
    public boolean isInterleave(String s1, String s2, String s3) {
        if (s1.length() + s2.length() != s3.length()) return false;
        if (s3.isEmpty()) return true;
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        return dsf(s1, 0, s2, 0, s3, 0, dp);
    }

    // 思考点，使用了 递归获取每个字符串的匹配的可能，也就枚举，但是有更简单的方法
    // 使用一次二维便利的数组？没想通.
    // 之前有一个很像，但是想不起来，需要再次学习
    public boolean dsf(String s1, int s1Index, String s2, int s2Index, String s3, int s3Index, int[][] dp) {
        if (s3Index == s3.length()) {
            return true;
        }
        if (dp[s1Index][s2Index] != 0) {
            return dp[s1Index][s2Index] == 1;
        }
        char c3 = s3.charAt(s3Index);
        if (s2Index < s2.length() && c3 == s2.charAt(s2Index)) {
            boolean b = dsf(s1, s1Index, s2, s2Index + 1, s3, s3Index + 1, dp);
            if (b) {
                return true;
            } else {
                dp[s1Index][s2Index] = -1;
            }
        }
        if (s1Index < s1.length() && c3 == s1.charAt(s1Index)) {
            boolean b = dsf(s1, s1Index + 1, s2, s2Index, s3, s3Index + 1, dp);
            if (b) return true;
            else dp[s1Index][s2Index] = -1;
        }
        return false;
    }

    public static void main(String[] args) {
        L_97 l97 = new L_97();
        System.out.println(l97.isInterleave("aabcc", "dbbca", "aadbbcbcac"));
        System.out.println(l97.isInterleave("aabcc", "dbbca", "aadbbbaccc"));
        System.out.println(l97.isInterleave("", "dbbca", "dbbca"));
        System.out.println(l97.isInterleave("aabcc", "dbbca", "aadbcbbcac")); // true
        System.out.println(l97.isInterleave("abababababababababababababababababababababababababababababababababababababababababababababababababbb",
                "babababababababababababababababababababababababababababababababababababababababababababababababaaaba",
                "abababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababbb")); // true
        System.out.println(l97.isInterleave("", "", ""));
    }
}
