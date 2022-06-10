package sample

func searchInsert(nums []int, target int) int {
	left := 0
	right := len(nums) - 1

	if target > nums[len(nums)-1] {
		return len(nums)
	}
	var mark int

	for left <= right {
		mid := (right-left)/2 + left
		if nums[mid] >= target {
			mark = mid
			right = mid - 1
		} else if nums[mid] < target {
			left = mid + 1
		}
	}

	return mark
}
