package sample

func maxSubArray(nums []int) int {
	max := nums[0]

	for i := 1; i < len(nums); i++ {
		if nums[i]+nums[i-1] > nums[i] {
			nums[i] = nums[i] + nums[i-1]
		}
		if nums[i] > max {
			max = nums[i]
		}
	}

	return max
}

// case: {5, 4, -1, 7, 8}
//func maxSubArray(nums []int) int {
//	sumMax := -100000
//	sumArr := make([]int, len(nums))
//	for i := 0; i < len(nums); i++ {
//		sumArr[i] = nums[i]
//
//		for j := 0; j < i; j++ {
//			sum := 0
//			for k := j; k < i; k++ {
//				sum += nums[k]
//			}
//			subMax := int(math.Max(float64(sumArr[j]), float64(sum+nums[i])))
//			if subMax > sumArr[i] {
//				sumArr[i] = subMax
//			}
//		}
//
//		if sumArr[i] > sumMax {
//			sumMax = sumArr[i]
//		}
//	}
//	return sumMax
//}
