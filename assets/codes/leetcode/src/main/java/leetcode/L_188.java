package leetcode;

public class L_188 {
    public int maxProfit(int k, int[] prices) {

        int[][] dp = new int[k + 1][2];
        for (int j = 0; j <= k; j++) {
            // dp[j][0] 默认初始化为 0
            // 将持有股票的状态初始化为极小值，表示一开始不可能凭空持有股票
            dp[j][1] = Integer.MIN_VALUE;
        }

        for (int i = 0; i < prices.length; i++) {
            for (int j = 1; j <= k; j++) {
                dp[j][1] = Math.max(dp[j][1], dp[j - 1][0] - prices[i]);
                dp[j][0] = Math.max(dp[j][0], dp[j][1] + prices[i]);
            }
        }

        return dp[k][0];
    }

    public static void main(String[] args) {
        L_188 l188 = new L_188();
        System.out.println(l188.maxProfit(2, new int[]{2, 4, 1})); // 2
        System.out.println(l188.maxProfit(2, new int[]{3, 2, 6, 5, 0, 3})); // 7
    }
}
