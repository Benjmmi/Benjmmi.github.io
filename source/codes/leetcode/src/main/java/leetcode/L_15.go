package sample

import "sort"

func threeSum(nums []int) [][]int {
	length := len(nums)
	if length < 3 {
		return [][]int{}
	}

	sort.Ints(nums)

}
