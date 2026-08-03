package top100liked_golang

import (
	"fmt"
	"testing"
)

func canJump(nums []int) bool {
	if len(nums) == 1 {
		return true
	}
	maxStep := nums[0]
	for i := 0; i < len(nums) && i <= maxStep; i++ {
		if i+nums[i] > maxStep {
			maxStep = i + nums[i]
		}
	}
	if maxStep >= len(nums)-1 {
		return true
	}

	return false
}

func Test_canJump(t *testing.T) {
	//fmt.Println(canJump([]int{2, 3, 1, 1, 4}))
	//fmt.Println(canJump([]int{3, 2, 1, 0, 4}))
	//fmt.Println(canJump([]int{2, 0, 0}))
	fmt.Println(canJump([]int{1, 2, 3}))
	//fmt.Println(canJump([]int{1, 0, 0}))
	//fmt.Println(canJump([]int{1, 0}))
}
