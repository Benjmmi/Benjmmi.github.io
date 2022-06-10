package sample

func maxProfit1(prices []int) int {
	if len(prices) <= 1 {
		return 0
	}
	min := prices[0]
	mount := 0
	for i := 1; i < len(prices); i++ {
		if prices[i] < min {
			min = prices[i]
		} else if prices[i] > min {
			if prices[i]-min > mount {
				mount = prices[i] - min
			}
		}
	}
	return mount
}
