package top100liked_golang

import (
	"fmt"
	"slices"
	"testing"
)

func lengthOfLIS(nums []int) int {
	maxLen := 0
	sortNums := []int{}
	maps := map[int]int{}
	for _, num := range nums {
		if _, ok := maps[num]; !ok {
			sortNums = append(sortNums, num)
			slices.Sort(sortNums)
		}
		index, ok := slices.BinarySearch(sortNums, num)
		if ok && index-1 >= 0 {
			for i := index - 1; i >= 0; i-- {
				maps[num] = max(maps[sortNums[i]]+1, maps[num])
			}
		} else {
			maps[num] = 1
		}
		maxLen = max(maxLen, maps[num])
	}
	return maxLen
}

func Test_lengthOfLIS(t *testing.T) {
	fmt.Println(lengthOfLIS([]int{3, 5, 6, 2, 5, 4, 19, 5, 6, 7, 12}))
	fmt.Println(lengthOfLIS([]int{10, 9, 2, 5, 3, 7, 101, 18}))
	fmt.Println(lengthOfLIS([]int{0, 1, 0, 3, 2, 3}))
	fmt.Println(lengthOfLIS([]int{7, 7, 7, 7, 7, 7, 7}))
}
