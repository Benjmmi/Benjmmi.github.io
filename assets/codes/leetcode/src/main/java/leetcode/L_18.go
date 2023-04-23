package sample

import "sort"

func fourSum(nums []int, target int) [][]int {
	sort.Ints(nums)
	length := len(nums)
	result := [][]int{}
	for i := 0; i < length-3 && nums[i]+nums[i+1]+nums[i+2]+nums[i+3] <= target; i++ {
		if i > 0 && nums[i] == nums[i-1] || nums[i]+nums[length-1]+nums[length-2]+nums[length-3] < target {
			continue
		}
		for j := i + 1; j < length-2 && nums[i]+nums[j]+nums[j+1]+nums[j+2] <= target; j++ {
			if j > i+1 && nums[j] == nums[j-1] || nums[i]+nums[j]+nums[length-1]+nums[length-2] < target {
				continue
			}
			for left, right := j+1, length-1; left < right; {
				if sum := nums[i] + nums[j] + nums[left] + nums[right]; sum < target {
					left++
				} else if sum > target {
					right--
				} else {
					result = append(result, []int{nums[i], nums[j], nums[left], nums[right]})
					for left++; left < right && nums[left] == nums[left-1]; left++ {

					}
					for right--; left < right && nums[right] == nums[right+1]; right-- {
					}
				}
			}
		}
	}
	return result
}
