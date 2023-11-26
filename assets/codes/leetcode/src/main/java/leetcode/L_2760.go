package sample

import "math"

func longestAlternatingSubarray(nums []int, threshold int) int {
	l := 0
	maxLen := 0
	subLen := 0
	for i := l; i < len(nums)-1; i++ {
		if !(nums[l]%2 == 0) {
			l += 1
			i = l
			subLen = 0
			continue
		}
		if !(nums[i]%2 != nums[i+1]%2) {
			l = i + 1
			i = i + 1
			subLen = 0
			continue
		}
		if !(nums[i] <= threshold) {
			l = i + 1
			i += 1
			subLen = 0
			continue
		}
		subLen++
		maxLen = int(math.Max(float64(maxLen), float64(subLen)))
	}
	return subLen
}
