package sample

func maxProfit3(prices []int) int {
	if len(prices) < 2 {
		return 0
	}
	n := len(prices)
	var profix = make([][3][2]int, n)

	profix[0][1][0] = 0
	profix[0][1][1] = -prices[0]
	profix[0][2][0] = 0
	profix[0][2][1] = -prices[0]
	for i := 1; i < n; i++ {
		profix[i][2][0] = max(profix[i-1][2][0], profix[i-1][2][1]+prices[i])
		profix[i][2][1] = max(profix[i-1][2][1], profix[i-1][1][0]-prices[i])

		profix[i][1][0] = max(profix[i-1][1][0], profix[i-1][1][1]+prices[i])
		profix[i][1][1] = max(profix[i-1][1][1], profix[i-1][0][0]-prices[i])
	}
	return profix[n-1][2][0]
}
