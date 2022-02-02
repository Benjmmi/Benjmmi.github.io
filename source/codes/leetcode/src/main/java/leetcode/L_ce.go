package sample

import "math"

func minCostClimbingStairs(cost []int) int {
	cost = append([]int{0}, cost...)
	return findMinCostClimbingStairs(cost, 0)
}

func findMinCostClimbingStairs(cost []int, cur int) int {
	if cur >= len(cost) {
		return 0
	}

	val := cost[cur] + findMinCostClimbingStairs(cost, cur+1)
	val2 := cost[cur] + findMinCostClimbingStairs(cost, cur+2)
	return int(math.Min(float64(val), float64(val2)))
}
