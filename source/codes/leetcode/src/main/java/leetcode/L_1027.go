package sample

import "fmt"

func longestArithSeqLength(nums []int) int {
	set := map[int][]int{}
	for i := 0; i < len(nums); i++ {
		if set[nums[i]] != nil {
			set[nums[i]] = append(set[nums[i]], i)
		} else {
			set[nums[i]] = []int{i}
		}
	}
	visited := map[string]int{}

	maxLen := 0
	for i := 0; i < len(nums); i++ {
		for k := i + 1; k < len(nums); k++ {
			rootNum := 1
			subVal := nums[k] - nums[i]
			pre := nums[i]
			for {
				if v, e := set[pre+subVal]; e {
					ac := -1
					for _, index := range v {
						if index > k {
							ac = index
							break
						}
					}
					if ac != -1 {
						rootNum++
						visited[fmt.Sprintf("%d_%d_%d", pre, pre+subVal, ac)] = rootNum
						pre += subVal
					} else {
						break
					}
				} else {
					break
				}
				if rootNum > maxLen {
					maxLen = rootNum
				}
			}
		}
	}
	return maxLen
}
