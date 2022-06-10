package sample

func findNumberOfLIS(nums []int) int {
	max := 0
	numArr := make([]int, len(nums))
	counts := make([]int, len(nums))
	for i := 0; i < len(counts); i++ {
		counts[i] = 1
	}
	for i := 0; i < len(nums); i++ {
		for j := 0; j < i; j++ {
			if nums[j] < nums[i] {
				if numArr[j] >= numArr[i] {
					numArr[i] = 1 + numArr[j]
					counts[i] = counts[j]
				} else if numArr[j]+1 == numArr[i] {
					counts[i] += counts[j]
				}
			}
		}
	}
	for i := 0; i < len(numArr); i++ {
		if numArr[i] > max {
			max = numArr[i]
		}
	}
	ans := 0
	for i := 0; i < len(numArr); i++ {
		if numArr[i] == max {
			ans += counts[i]
		}
	}

	return ans
}
