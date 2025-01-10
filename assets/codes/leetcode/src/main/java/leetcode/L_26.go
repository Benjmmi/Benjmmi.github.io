package sample

func removeDuplicates(nums []int) int {
	if len(nums) <= 1 {
		return len(nums)
	}

	l := len(nums)
	index := 0
	mark := 0
	for i := 1; i < l; i++ {
		if nums[i] != nums[index] {
			mark++
			nums[mark] = nums[i]
			index = i
		}
	}

	return mark + 1
}
