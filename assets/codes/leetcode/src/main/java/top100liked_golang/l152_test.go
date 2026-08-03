package top100liked_golang

import (
	"fmt"
	"testing"
)

func maxProduct(nums []int) int {
	minArr := make([]int, len(nums))
	maxArr := make([]int, len(nums))
	minArr[0] = nums[0]
	maxArr[0] = nums[0]
	maxVal := nums[0]

	for i := 1; i < len(nums); i++ {
		minArr[i] = min(minArr[i-1]*nums[i], maxArr[i-1]*nums[i], nums[i-1]*nums[i], nums[i])
		maxArr[i] = max(minArr[i-1]*nums[i], maxArr[i-1]*nums[i], nums[i-1]*nums[i], nums[i])
		maxVal = max(maxArr[i], minArr[i], nums[i], maxVal)
	}

	return maxVal
}

func Test_maxProduct(t *testing.T) {
	fmt.Println(maxProduct([]int{2, 3, -2, 4}))
	fmt.Println(maxProduct([]int{-2, 0, -1}))
}
