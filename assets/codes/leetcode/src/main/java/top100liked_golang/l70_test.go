package top100liked_golang

import "testing"

func climbStairs(n int) int {
	pre := 1
	if n == 1 {
		return pre
	}
	ans := 2
	for i := 2; i < n; i++ {
		pre, ans = ans, pre+ans
	}
	return ans
}

func Test_climbStairs(t *testing.T) {

}
