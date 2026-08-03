package top100liked_golang

import (
	"fmt"
	"testing"
)

func jump(nums []int) int {
	if len(nums) == 1 {
		return 0
	}
	steps := 0

	start := 0
	end := 0
	for end < len(nums)-1 {
		nextStart := start
		nextEnd := end
		for i := start; i <= end && i < len(nums); i++ {
			if i+nums[i] > nextEnd {
				nextEnd = i + nums[i]
				nextStart = i
			}
		}
		start = nextStart
		end = nextEnd
		steps++
	}

	return steps
}

func Test_jump(t *testing.T) {
	fmt.Println(jump([]int{2, 3, 1, 1, 4}))                               // 2
	fmt.Println(jump([]int{2, 3, 0, 1, 4}))                               // 2
	fmt.Println(jump([]int{2}))                                           // 0
	fmt.Println(jump([]int{2, 0, 0}))                                     // 1
	fmt.Println(jump([]int{7, 0, 9, 6, 9, 6, 1, 7, 9, 0, 1, 2, 9, 0, 3})) // 2
}
