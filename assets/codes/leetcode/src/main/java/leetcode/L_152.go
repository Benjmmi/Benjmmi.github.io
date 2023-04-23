package sample

func maxProduct(nums []int) int {
	if len(nums) < 1 {
		return 0
	}

	maxF := make([]int, len(nums))
	minF := make([]int, len(nums))
	maxF[0] = nums[0]
	minF[0] = nums[0]
	for i := 1; i < len(nums); i++ {
		maxF[i] = max(maxF[i-1]*nums[i], max(nums[i], minF[i-1]*nums[i]))

		minF[i] = min(minF[i-1]*nums[i], min(nums[i], maxF[i-1]*nums[i]))
	}

	maxV := maxF[0]
	for i := range maxF {
		maxV = max(maxF[i], maxV)
	}
	return maxV
}

//func maxProduct(nums []int) int {
//	maxF, minF, ans := nums[0], nums[0], nums[0]
//	for i := 1; i < len(nums); i++ {
//		mx, mn := maxF, minF
//		maxF = max(mx*nums[i], max(nums[i], mn*nums[i]))
//		minF = min(mn*nums[i], min(nums[i], mx*nums[i]))
//		ans = max(maxF, ans)
//	}
//	return ans
//}

func max(x, y int) int {
	if x > y {
		return x
	}
	return y
}

func min(x, y int) int {
	if x < y {
		return x
	}
	return y
}
