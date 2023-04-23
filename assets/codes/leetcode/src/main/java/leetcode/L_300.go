package sample

import "math"

func lengthOfLIS(nums []int) int {

	if len(nums) < 2 {
		return len(nums)
	}

	max := 0
	val := make([]int, len(nums))
	for i := 0; i < len(nums); i++ {
		val[i] = 1
		for j := 0; j < i; j++ {
			if nums[j] < nums[i] {
				val[i] = int(math.Max(float64(val[i]), float64(val[j]+1)))
			}
		}
		max = int(math.Max(float64(val[i]), float64(max)))
	}

	return max
}
