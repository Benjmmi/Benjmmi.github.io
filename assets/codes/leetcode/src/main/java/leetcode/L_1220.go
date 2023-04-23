package sample

// 1 - 5
// 2 - 10
// 3 - 19
// 4 - 35
// 5 - 68
// 6 - 129
// 7 - 249
func countVowelPermutation(n int) int {
	mod := int(1e9 + 7)
	nums := []int{1, 1, 1, 1, 1}
	for i := 1; i < n; i++ {
		nums = []int{
			(nums[1] + nums[2] + nums[4]) % mod,
			(nums[0] + nums[2]) % mod,
			(nums[1] + nums[3]) % mod,
			nums[2],
			(nums[2] + nums[3]) % mod,
		}
	}
	var ans int
	for _, v := range nums {
		ans = (ans + v) % mod
	}
	return ans
}
