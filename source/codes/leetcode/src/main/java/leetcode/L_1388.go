package sample

func maxSizeSlices(slices []int) int {

	start := slices[1:]
	end := slices[:len(slices)-1]

	return max(clc(start), clc(end))
}
func clc(slices []int) int {
	length := len(slices)
	ch := (length + 1) / 3
	dp := make([][]int, length+1)
	for i := range dp {
		dp[i] = make([]int, ch+1)
	}

	for i := 1; i <= length; i++ {
		for j := 1; j <= ch; j++ {
			var dpij int
			if i-2 >= 0 {
				dpij = dp[i-2][j-1]
			}
			dp[i][j] = max(dp[i-1][j], dpij+slices[i-1])
		}
	}
	return dp[length][ch]
}
