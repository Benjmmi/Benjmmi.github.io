package top100liked_golang

import "testing"

func searchInsert(nums []int, target int) int {
	left, right := 0, len(nums)-1
	ans := len(nums)

	for left <= right {
		mid := left + (right-left)>>1
		if nums[mid] >= target {
			ans = mid
			right = mid - 1
		} else {
			left = mid + 1
		}
	}
	return ans
}

func Test_searchInsert(t *testing.T) {
	
}
