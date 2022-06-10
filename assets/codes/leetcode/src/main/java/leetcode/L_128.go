package sample

import (
	"math"
	"sort"
)

func longestConsecutive(nums []int) int {
	if len(nums) == 0 {
		return 0
	}
	sort.Ints(nums)
	len := len(nums)
	max := 1
	curLen := 1
	for i := 1; i < len; i++ {
		if nums[i]-nums[i-1] == 1 {
			curLen++
		} else if nums[i]-nums[i-1] == 0 {

		} else {
			max = int(math.Max(float64(max), float64(curLen)))
			curLen = 1
		}
	}
	max = int(math.Max(float64(max), float64(curLen)))
	return max
}
