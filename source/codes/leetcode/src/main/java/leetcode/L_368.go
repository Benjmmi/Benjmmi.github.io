package sample

import "sort"

func largestDivisibleSubset(nums []int) []int {
	if len(nums) == 1 {
		return nums
	}
	sort.Ints(nums)
	rootx := make([]int, len(nums))
	total := make([]int, len(nums))
	for i := range rootx {
		total[i] = 1
	}
	max := 0
	markMax := 0
	for i := 0; i < len(nums); i++ {
		for j := i + 1; j < len(nums); j++ {
			if nums[j]%nums[i] == 0 {
				if total[i]+1 > total[j] {
					total[j] = total[i] + 1
					rootx[j] = i
				}
				if total[j] > max {
					max = total[j]
					rootx[j] = i
					markMax = j
				}
			}
		}
	}
	result := []int{}
	for max != 0 {
		max--
		result = append(result, nums[markMax])
		markMax = rootx[markMax]
	}
	if len(result) == 0 {
		return []int{nums[0]}
	}
	sort.Ints(result)
	return result
}

//func largestDivisibleSubset(nums []int) []int {
//	if len(nums) == 1 {
//		return nums
//	}
//	sort.Ints(nums)
//	result := map[int][]int{}
//	for i := 0; i < len(nums); i++ {
//		result[nums[i]] = []int{nums[i]}
//	}
//	max := []int{nums[0]}
//	for i := 0; i < len(nums); i++ {
//		for j := i + 1; j < len(nums); j++ {
//			if nums[j]%nums[i] == 0 {
//				if len(result[nums[i]])+1 > len(result[nums[j]]) {
//					dst := make([]int, len(result[nums[i]]))
//					copy(dst, result[nums[i]])
//					result[nums[j]] = append(dst, nums[j])
//				}
//				if len(result[nums[j]]) > len(max) {
//					max = result[nums[j]]
//				}
//				if len(max) == len(nums) {
//					return max
//				}
//			}
//		}
//	}
//	return max
//}
