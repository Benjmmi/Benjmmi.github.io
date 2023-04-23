package sample

func longestCommonSubsequence(text1 string, text2 string) int {
	m, n := len(text1), len(text2)
	if n > m {
		text1, text2 = text2, text1
		m, n = n, m
	}
	arr := make([][]int, m+1)
	for i := 0; i <= m; i++ {
		arr[i] = make([]int, n+1)
	}
	maxLength := 0
	for i := 1; i <= m; i++ {
		for j := 1; j <= n; j++ {
			if text1[i-1] != text2[j-1] {
				arr[i][j] = max(arr[i][j-1], arr[i-1][j])
			} else {
				arr[i][j] = arr[i-1][j-1] + 1
			}
			if arr[i][j] > maxLength {
				maxLength = arr[i][j]
			}
		}
	}
	return maxLength
}
