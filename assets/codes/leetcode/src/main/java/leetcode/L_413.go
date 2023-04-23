package sample

func numberOfArithmeticSlices(nums []int) int {
	if len(nums) < 3 {
		return 0
	}
	subVal := nums[1] - nums[0]
	length := 0
	maxLength := 0
	for i := 2; i < len(nums); i++ {
		if nums[i]-nums[i-1] == subVal {
			length++
		} else {
			subVal = nums[i] - nums[i-1]
			length = 0
		}
		maxLength += length
	}

	return maxLength
}
