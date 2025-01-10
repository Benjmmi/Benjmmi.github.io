package sample

import "sort"

func merge(nums1 []int, m int, nums2 []int, n int) {
	nums := []int{}
	if len(nums1) >= 0 {
		nums = nums1[:m]
	}
	nums = append(nums, nums2...)
	sort.Slice(nums, func(i, j int) bool {
		return nums[i] < nums[j]
	})
}
