package leetcode;

public class L_72 {
    public int minDistance(String word1, String word2) {

        if (word1.equals(word2)) {
            return 0;
        }
        if (word1.isEmpty()) {
            return word2.length();
        }
        if (word2.isEmpty()) {
            return word1.length();
        }

        int[][] dp = new int[word1.length() + 1][word2.length() + 1];
        for (int i = 0; i < word1.length(); i++) {
            dp[i][0] = i;
        }
        for (int i = 0; i < word2.length(); i++) {
            dp[0][i] = i;
        }

//        for (int i = 0; i < word1.length(); i++) {
//            for (int j = 0; j < word2.length(); j++) {
//                if (word1.charAt(i) == word2.charAt(j)) {
//                    dp[i][j] = Math.min(Math.min(dp[i - 1][j - 1], dp[i - 1][j]), dp[i][j - 1]);
//                } else {
//                    dp[i][j] = Math.min(Math.min(dp[i - 1][j - 1], dp[i - 1][j]), dp[i][j - 1]) + 1;
//                }
//            }
//        }

        return dsf(word1, word2, word1.length(), word2.length(), dp);
    }

    public int dsf(String word1, String word2, int i, int j, int[][] dp) {
        if (i - 1 < 0) {
            return dp[0][j];
        }
        if (j - 1 < 0) {
            return dp[i][0];
        }
        if (dp[i][j] != 0) {
            return dp[i][j];
        }
        if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
            dp[i][j] = dsf(word1, word2, i - 1, j - 1, dp);
        } else {
            int val1 = dsf(word1, word2, i, j - 1, dp);
            int val2 = dsf(word1, word2, i - 1, j, dp);
            int val3 = dsf(word1, word2, i - 1, j - 1, dp);
            dp[i][j] = 1 + Math.min(Math.min(val1, val2), val3);
        }
        return dp[i][j];
    }

    public static void main(String[] args) {
        L_72 l72 = new L_72();
//        System.out.println(l72.minDistance("horse", "ros"));
//        System.out.println(l72.minDistance("intention", "execution"));
        System.out.println(l72.minDistance("distance", "springbok"));
    }
}
