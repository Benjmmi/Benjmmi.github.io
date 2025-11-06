package top100liked;

import java.util.Arrays;

public class L_62 {

    public int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];
        Arrays.fill(dp[0], 1);
        for (int i = 0; i < m; i++) {
            dp[i][0] = 1;
        }

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
            }
        }
        return dp[m-1][n-1];
    }

    public static void main(String[] args) {
        L_62 l62 = new L_62();
        System.out.println(l62.uniquePaths(3, 7));
        System.out.println(l62.uniquePaths(3, 2));
        System.out.println(l62.uniquePaths(7, 3));
        System.out.println(l62.uniquePaths(3, 3));
    }
}
