package sample

func maxProfit2(prices []int) int {
	if len(prices) <= 1 {
		return 0
	}
	// 设置第一个值为起始点
	min := prices[0]
	// 获取的利润总和
	sum := 0
	// 设置当前最大差价为 0
	preMaxMount := 0

	for i := 1; i < len(prices); i++ {
		// 如果当前差价已经小于最大差价了，说明已经是前一段时间的最大利润差了
		// 参考 1,5,3,6
		// 1-5 差价 = 4
		// 1-3 差价 = 2
		// 说明 1-5 已经是之前获取的最大插件了
		// 从 3 开始从新计算差价
		// 3-6 差价 = 2
		// 最大利润和为 4+3
		if prices[i]-min < preMaxMount {
			// 利润总和 + 当前最大利润
			sum += preMaxMount
			// 并把当前位置设置为最小值，新的差价起始点，完后推算
			min = prices[i]
			// 设置当前最大差价为0
			preMaxMount = 0
		} else if prices[i]-min > preMaxMount {
			// 如果当前差价，大于最大差价，就覆盖
			preMaxMount = prices[i] - min
		}
	}
	return sum + preMaxMount
}

//
//// 7, 1, 5, 3, 6, 4
//func maxProfit2(prices []int) int {
//	l := len(prices)
//	dp := make([][]int, len(prices))
//	for i := 0; i < l; i++ {
//		dp[i] = make([]int, 2)
//	}
//	dp[0][0] = 0
//	dp[0][1] = -prices[0]
//	for i := 1; i < l; i++ {
//		// 第 i 天进行的操作只能是休息或卖出 = max(T[i - 1][k][0] 休息操作可以得到的最大收益, dp[i-1][1]+prices[i] 卖出操作可以得到的最大收益)
//		dp[i][0] = max(dp[i-1][0], dp[i-1][1]+prices[i])
//		// 第 i 天进行的操作只能是休息或买入 = max(dp[i-1][1] 是休息操作可以得到的最大收益,dp[i-1][0]-prices[i] 是买入操作可以得到的最大收益)
//		dp[i][1] = max(dp[i-1][1], dp[i-1][0]-prices[i])
//	}
//	return dp[l-1][0]
//}
