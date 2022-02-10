package sample

func maxProfit(prices []int, fee int) int {
	n := len(prices)
	if n < 2 {
		return 0
	}
	profit := make([][2]int, n)
	profit[0][0] = 0
	profit[0][1] = -prices[0] - fee
	for i := 1; i < n; i++ {
		profit[i][0] = max(profit[i-1][0], profit[i-1][1]+prices[i])
		profit[i][1] = max(profit[i-1][1], profit[i-1][0]-prices[i]-fee)
	}
	return profit[n-1][0]
}
