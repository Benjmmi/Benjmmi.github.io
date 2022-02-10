package sample

func longestValidParentheses(s string) int {
	maxAns := 0
	dp := make([]int, len(s))
	for i := 1; i < len(s); i++ {
		if s[i] == ')' {
			if s[i-1] == '(' {
				if i >= 2 {
					dp[i] = dp[i-2] + 2
				} else {
					dp[i] = 2
				}
			} else if i-dp[i-1] > 0 && s[i-dp[i-1]-1] == '(' {
				if i-dp[i-1] >= 2 {
					dp[i] = dp[i-1] + dp[i-dp[i-1]-2] + 2
				} else {
					dp[i] = dp[i-1] + 2
				}
			}
		}
		maxAns = max(maxAns, dp[i])
	}
	return maxAns
}

//func longestValidParentheses(s string) int {
//
//	if len(s) < 2 {
//		return 0
//	}
//	mark := make([]int, len(s))
//	stack := []int{}
//	right := 0
//	for i := 0; i < len(s); i++ {
//		if s[i] == ')' {
//			if len(stack) == 0 {
//				right++
//				continue
//			}
//			index := stack[len(stack)-1]
//			stack = stack[:len(stack)-1]
//			mark[i] = 1
//			mark[index] = 1
//		} else {
//			stack = append(stack, i)
//		}
//	}
//	max := 0
//	for i := 1; i < len(mark); i++ {
//		if mark[i] == 1 {
//			mark[i] = mark[i-1] + 1
//			if mark[i] > max {
//				max = mark[i]
//			}
//		}
//	}
//
//	return max
//}
