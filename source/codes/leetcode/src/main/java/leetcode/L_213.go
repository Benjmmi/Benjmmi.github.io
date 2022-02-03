package sample

func rob(nums []int) int {
	if len(nums) == 0 {
		return 0
	}
	if len(nums) == 1 {
		return nums[0]
	}
	if len(nums) == 2 {
		if nums[0] > nums[1] {
			return nums[0]
		} else {
			return nums[1]
		}
	}
	sums1 := make([]int, len(nums))
	sums2 := make([]int, len(nums))
	sums1[0] = nums[0]
	sums2[0] = 0

	maxVal := nums[0]
	sums1[1] = max(nums[0], nums[1])
	sums2[1] = nums[1]
	for i := 2; i < len(nums); {
		if i < len(nums)-1 {
			sums1[i] = max(sums1[i-1], sums1[i-2]+nums[i])
		}
		sums2[i] = max(sums2[i-1], sums2[i-2]+nums[i])
		if sums1[i] > maxVal {
			maxVal = sums1[i]
		}
		if sums2[i] > maxVal {
			maxVal = sums2[i]
		}
		i++
	}

	return maxVal
}
