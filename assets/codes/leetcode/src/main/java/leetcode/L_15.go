package sample

import (
	"sort"
)

func threeSum(nums []int) [][]int {
	length := len(nums)
	var result [][]int
	if length < 3 {
		return result
	}
	sort.Ints(nums)

	if nums[0] > 0 {
		return result
	}

	if nums[length-1] < 0 {
		return result
	}
	for i, _ := range nums {
		if i > 0 && nums[i-1] == nums[i] {
			// 跳过重复的元素
			continue
		}
		L := i + 1
		R := length - 1
		for L < R {
			if nums[i]+nums[L]+nums[R] == 0 {
				result = append(result, []int{nums[i], nums[L], nums[R]})
				for L < R && nums[L] == nums[L+1] {
					L++
				}
				for R > L && nums[R] == nums[R-1] {
					R--
				}
				L++
				R--
			} else if nums[i]+nums[L]+nums[R] > 0 {
				R--
			} else {
				L++
			}
		}
	}
	return result
}

//type tree struct {
//	x, y, z int
//}
//
//func threeSum(nums []int) [][]int {
//	length := len(nums)
//	var result [][]int
//	if length < 3 {
//		return result
//	}
//	sort.Ints(nums)
//
//	if nums[0] > 0 {
//		return result
//	}
//
//	if nums[length-1] < 0 {
//		return result
//	}
//	sumMap := map[int][][]int{}
//	resultMap := map[tree]bool{}
//	// 压缩 0
//	for i := 3; i < length; {
//		if nums[i-1] == nums[i-2] && nums[i-2] == nums[i-3] {
//			count := 0
//			for j := i; j < length; j++ {
//				if nums[j] == nums[i-3] {
//					count++
//				} else {
//					break
//				}
//			}
//			index := (i + count)
//			nums = append(nums[:i], nums[index:]...)
//			length = len(nums)
//		}
//		i++
//	}
//	for i := 0; i < length; i++ {
//		for j := i + 1; j < length; j++ {
//
//			v := nums[i] + nums[j]
//			index := []int{i, j}
//			if _, e := sumMap[v]; e {
//				sumMap[v] = append(sumMap[v], index)
//			} else {
//				sumMap[v] = [][]int{index}
//			}
//		}
//	}
//
//	for i := 0; i < length; i++ {
//		if nums[i] > 0 {
//			break
//		}
//		arr, exist := sumMap[-nums[i]]
//		if exist {
//			for _, ints := range arr {
//				b := false
//				// O(3n) -> O(1)
//				if ints[0] == i || ints[1] == i {
//					b = true
//				}
//				if !b {
//					r := []int{nums[ints[0]], nums[ints[1]], nums[i]}
//					sort.Ints(r)
//					t := tree{r[0], r[1], r[2]}
//					if _, e := resultMap[t]; !e {
//						resultMap[t] = true
//						result = append(result, r)
//					}
//				}
//			}
//		}
//	}
//
//	return result
//}
