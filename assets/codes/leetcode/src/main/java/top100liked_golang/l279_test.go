package top100liked_golang

import (
	"fmt"
	"math"
	"testing"
)

func numSquares(n int) int {
	ans := make([]int, n+1)
	for i := 1; i <= n; i++ {
		minV := math.MaxInt32
		for j := 1; j*j <= i; j++ {
			minV = min(minV, ans[i-j*j])
		}
		ans[i] = minV + 1
	}
	return ans[n]
}

func findNumSquares(n int, nums []int, ans []int) (bool, []int) {
	if n == 0 {
		return true, ans
	}
	if n < 0 {
		return false, ans
	}
	length := len(nums) - 1
	for i := length; i >= 0; i-- {
		if n-nums[i] < 0 {
			continue
		}
		ans = append(ans, nums[i])
		valid, ans := findNumSquares(n-nums[i], nums, ans)
		if valid {
			return valid, ans
		}
		ans = ans[:len(ans)-1]
	}

	return false, ans
}

func Test_numSquares(t *testing.T) {
	//fmt.Println(numSquares(1))
	//fmt.Println(numSquares(2))
	fmt.Println(numSquares(3))
	//fmt.Println(numSquares(12))
}
