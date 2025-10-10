package leetcode;

public class L_221 {
    public int maximalSquare(char[][] matrix) {

        int[][] dp = new int[matrix.length + 1][matrix[0].length + 1];

        int max = 0;

        for (int i = 1; i <= matrix.length; i++) {
            for (int j = 1; j <= matrix[0].length; j++) {
                if (matrix[i - 1][j - 1] == '0') {
                    continue;
                }
                dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                max = Math.max(max, dp[i][j] * dp[i][j]);
            }
        }

        return max;
    }

    public static void main(String[] args) {
        L_221 l221 = new L_221();
//        System.out.println(l221.maximalSquare(new char[][]{{'1', '0', '1', '0', '0'}, {'1', '0', '1', '1', '1'}, {'1', '1', '1', '1', '1'}, {'1', '0', '0', '1', '0'}}));
        System.out.println(l221.maximalSquare(new char[][]{{'1'}}));
        System.out.println(l221.maximalSquare(new char[][]{{'0'}, {'1'}}));
    }
}
