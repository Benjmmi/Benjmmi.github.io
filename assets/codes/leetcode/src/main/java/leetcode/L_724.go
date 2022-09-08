package sample

func pivotIndex(nums []int) int {
	if len(nums) <= 1 {
		return 0
	}
	ns := []int{0}
	ns = append(ns, nums...)
	nums = ns
	sum := 0
	for i := range nums {
		sum += nums[i]
	}

	b := 0
	for i, num := range nums {
		if i == len(nums)-1 {
			return -1
		}
		b += num
		if b == sum-b-nums[i+1] {
			return i
		}
	}
	return -1
}
