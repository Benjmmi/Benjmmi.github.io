package top100liked_golang

func maxProfit(prices []int) int {
	ans := 0
	minV := prices[0]
	for i := 0; i < len(prices); i++ {
		ans = max(ans, prices[i]-minV)
		minV = min(minV, prices[i])
	}

	return ans
}
