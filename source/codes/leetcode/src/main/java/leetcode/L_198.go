package sample

func rob(nums []int) int {
	if len(nums) == 0 {
		return 0
	}
	if len(nums) == 1 {
		return nums[0]
	}
	sums := make([]int, len(nums))
	sums[0] = nums[0]
	sums[1] = max(nums[0], nums[1])
	maxV := max(sums[0], sums[1])
	for i := 2; i < len(nums); i++ {
		sums[i] = max(sums[i-2]+nums[i], sums[i-1])
		maxV = max(sums[i], maxV)
	}

	return maxV
}

//func rob(nums []int) int {
//	s1 := robsum(nums, 0)
//	s2 := robsum(nums, 1)
//	if s2 > s1 {
//		return s2
//	}
//	return s1
//}
//
//func robsum(nums []int, cur int) int {
//	if cur >= len(nums) {
//		return 0
//	}
//	return nums[cur] + max(robsum(nums, cur+2), robsum(nums, cur+3))
//}
