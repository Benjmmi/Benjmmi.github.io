package sample

func numDecodings(s string) int {
	// 2226
	length := len(s)
	ans := make([]int, length+1)
	ans[0] = 1
	for i := 1; i <= length; i++ {
		if s[i-1] != '0' {
			ans[i] += ans[i-1]
		}
		if i > 1 && s[i-2] != '0' && ((s[i-2]-'0')*10+s[i-1]-'0') <= 26 {
			ans[i] += ans[i-2]
		}
	}
	return ans[length]
}
