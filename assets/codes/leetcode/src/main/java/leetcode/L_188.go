package sample

func maxProfit4(k int, prices []int) int {
	if len(prices) < 2 {
		return 0
	}
	n := len(prices)
	var profit = make([][][]int, n)
	for i := range profit {
		profit[i] = make([][]int, k+1)
		for j := 0; j <= k; j++ {
			profit[i][j] = make([]int, 2)
			profit[i][j][0] = 0
			profit[i][j][1] = -prices[0]
		}
	}
	for i := 1; i < n; i++ {
		for j := k; j > 0; j-- {
			profit[i][j][0] = max(profit[i-1][j][0], profit[i-1][j][1]+prices[i])
			profit[i][j][1] = max(profit[i-1][j][1], profit[i-1][j-1][0]-prices[i])
		}
	}

	return profit[n-1][k][0]
}
