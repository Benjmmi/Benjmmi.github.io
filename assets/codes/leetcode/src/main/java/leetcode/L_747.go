package sample

import (
	"sort"
)

func dominantIndex(nums []int) int {
	l := len(nums)

	if l <= 1 {
		return 0
	}
	b := make([]int, len(nums))
	copy(b, nums)
	sort.Ints(nums)
	max := nums[l-1]
	max2 := nums[l-2]
	if max >= max2*2 {
		for i, i2 := range b {
			if i2 == max {
				return i
			}
		}
	}

	return -1
}
