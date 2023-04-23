package sample

// 7, 1, 5, 3, 6, 4
func maxProfit2(prices []int) int {
	l := len(prices)
	dp := make([][]int, len(prices))
	for i := 0; i < l; i++ {
		dp[i] = make([]int, 2)
	}
	dp[0][0] = 0
	dp[0][1] = -prices[0]
	for i := 1; i < l; i++ {
		// 第 i 天进行的操作只能是休息或卖出 = max(T[i - 1][k][0] 休息操作可以得到的最大收益, dp[i-1][1]+prices[i] 卖出操作可以得到的最大收益)
		dp[i][0] = max(dp[i-1][0], dp[i-1][1]+prices[i])
		// 第 i 天进行的操作只能是休息或买入 = max(dp[i-1][1] 是休息操作可以得到的最大收益,dp[i-1][0]-prices[i] 是买入操作可以得到的最大收益)
		dp[i][1] = max(dp[i-1][1], dp[i-1][0]-prices[i])
	}
	return dp[l-1][0]
}
