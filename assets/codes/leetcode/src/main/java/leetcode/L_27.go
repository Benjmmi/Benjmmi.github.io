package sample

func removeElement(nums []int, val int) int {
	l := len(nums)
	index := 0
	for i := 0; i < l; i++ {
		nums[index] = nums[i]
		if nums[i] == val {
			index--
		}
		index++
	}
	return index
}
