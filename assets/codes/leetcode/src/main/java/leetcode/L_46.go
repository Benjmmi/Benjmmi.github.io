package sample

import (
	"strconv"
	"strings"
)

func permute(nums []int) [][]int {
	result := [][]int{}
	arrmap := map[string][]int{}
	for i, _ := range nums {
		arr := make([]int, len(nums))
		arr[0] = i
		permutes(nums, arr, 0, strconv.Itoa(i), arrmap)
	}
	for _, i := range arrmap {
		result = append(result, i)
	}
	return result
}

func permutes(nums, arr []int, cur int, curStr string, arrmap map[string][]int) {
	if cur == len(nums)-1 {
		v := make([]int, len(arr))
		for i := 0; i < len(arr); i++ {
			v[i] = nums[arr[i]]
		}
		arrmap[curStr] = v
		return
	}
	cur++
	for i, _ := range nums {
		if strings.Contains(curStr, strconv.Itoa(i)) {
			continue
		}
		arr[cur] = i
		permutes(nums, arr, cur, curStr+strconv.Itoa(i), arrmap)
	}
}
