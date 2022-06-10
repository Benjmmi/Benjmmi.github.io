package sample

func longestSubarray(nums []int, limit int) int {
	var minQ, maxQ []int
	left := 0
	var ans = 0
	for right, value := range nums {
		for len(minQ) > 0 && minQ[len(minQ)-1] > value {
			minQ = minQ[:len(minQ)-1]
		}
		minQ = append(minQ, value)
		for len(maxQ) > 0 && maxQ[len(maxQ)-1] < value {
			maxQ = maxQ[:len(maxQ)-1]
		}
		maxQ = append(maxQ, value)
		for len(minQ) > 0 && len(maxQ) > 0 && maxQ[0]-minQ[0] > limit {
			if nums[left] == minQ[0] {
				minQ = minQ[1:]
			}
			if nums[left] == maxQ[0] {
				maxQ = maxQ[1:]
			}
			left++
		}
		ans = max(ans, right-left+1)
	}
	return ans
}
