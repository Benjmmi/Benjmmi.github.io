package sample

import "math"

func findKthLargest(nums []int, k int) int {

	if len(nums) == 1 {
		return nums[0]
	}

	maxHeap := make([]int, k)
	for i := 0; i < k; i++ {
		maxHeap[i] = math.MinInt
	}
	maxHeap[0] = nums[0]

	for i := range nums {
		num := nums[i]
		for j := 0; j < len(maxHeap); {
			if num > maxHeap[j] {
				b := maxHeap[j]
				maxHeap[j] = num
				if b == math.MinInt {
					break
				}
				num = b
				j = j*2 + 1
			} else if num < maxHeap[0] {
				if j == 0 {
					j = 1
				} else {
					j = j * 2
				}
			} else {
				if j == 0 {
					j = 1
				} else {
					j = j * 2
				}
			}
		}
	}
	if k == 1 {
		return maxHeap[0]
	}
	return min(maxHeap[len(maxHeap)-1], maxHeap[len(maxHeap)-2])
}
