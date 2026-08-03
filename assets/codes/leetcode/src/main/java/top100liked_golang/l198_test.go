package top100liked_golang

import (
	"fmt"
	"testing"
)

func rob(nums []int) int {
	n := len(nums)

	for i := 0; i < n; i++ {
		if i < 1 {
			nums[i] = nums[i]
			continue
		}
		if i < 2 {
			nums[i] = max(nums[i], nums[i-1])
			continue
		}
		nums[i] = max(nums[i]+nums[i-2], nums[i-1])
	}

	return nums[len(nums)-1]
}

func Test_rob(t *testing.T) {
	fmt.Println(rob([]int{1, 2, 3, 1}))
	fmt.Println(rob([]int{2, 7, 9, 3, 1}))
}
