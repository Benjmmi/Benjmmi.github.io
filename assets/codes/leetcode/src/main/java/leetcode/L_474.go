package sample

import "strings"

func findMaxForm(strs []string, m int, n int) int {
	l := len(strs)
	dp := make([][][]int, l+1)
	for i := range dp {
		dp[i] = make([][]int, m+1)
		for d := range dp[i] {
			dp[i][d] = make([]int, n+1)
		}
	}
	for i, s := range strs {
		zeros := strings.Count(s, "0")
		ones := len(s) - zeros
		for s := 0; s <= m; s++ {
			for k := 0; k <= n; k++ {
				dp[i+1][s][k] = dp[i][s][k]
				if zeros < m && ones < n {
					dp[i+1][s][k] = max(dp[i+1][s][k], dp[i][s-zeros][k-ones]+1)
				}
			}
		}
	}
	return dp[l][m][n]
}
