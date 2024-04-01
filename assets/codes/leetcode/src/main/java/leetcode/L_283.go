package sample

func moveZeroes(nums []int) {
	nonZero := 0
	for _, num := range nums {
		if num != 0 {
			nums[nonZero] = num
			nonZero++
		}
	}
	for i := nonZero; i < len(nums); i++ {
		nums[i] = 0
	}
	return
}
