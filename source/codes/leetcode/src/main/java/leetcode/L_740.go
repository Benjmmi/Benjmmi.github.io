package sample

func deleteAndEarn(nums []int) int {

	if len(nums) == 0 {
		return 0
	}
	if len(nums) == 1 {
		return nums[0]
	}

	earn := make([]int, 10000)

	for _, num := range nums {
		earn[num] += num
	}

	sums := make([]int, 10000)

	sums[0] = earn[0]
	sums[1] = max(earn[1], sums[0])

	maxVal := earn[0]

	for i := 2; i < len(earn); i++ {
		sums[i] = max(sums[i-1], sums[i-2]+earn[i])
		if sums[i] > maxVal {
			maxVal = sums[i]
		}
	}

	return maxVal
}
