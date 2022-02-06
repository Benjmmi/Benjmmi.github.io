package sample

func minCostClimbingStairs(cost []int) int {
	totalCost := make([]int, len(cost)+1)
	totalCost[0] = 0
	totalCost[1] = 0
	for i := 2; i <= len(cost); i++ {
		totalCost[i] = min(totalCost[i-1]+cost[i-1], totalCost[i-2]+cost[i-2])
	}
	return totalCost[len(cost)]
}
