package sample

func containsNearbyDuplicate(nums []int, k int) bool {
	for i, num := range nums {
		for j := 1; i+j < len(nums) && j <= k; j++ {
			if num == nums[i+j] {
				return true
			}
		}
	}
	return false
}
