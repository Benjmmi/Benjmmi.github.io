package top100liked_golang

import (
	"fmt"
	"slices"
	"sort"
	"testing"
)

func coinChange(coins []int, amount int) int {
	if amount == 0 {
		return 0
	}
	sort.Ints(coins)
	ans := map[int]int{}
	for i := 1; i <= amount; i++ {
		ans[i] = -1
		if slices.Contains(coins, i) {
			ans[i] = 1
			continue
		}

		minV := -1
		for j := 0; j < len(coins) && coins[j] <= i; j++ {
			if ans[i-coins[j]] == -1 {
				continue
			}
			if minV == -1 {
				minV = ans[i-coins[j]] + 1
			} else {
				minV = min(minV, ans[i-coins[j]]+1)
			}
		}
		ans[i] = minV
	}

	if _, ok := ans[amount]; ok {
		return ans[amount]
	}
	return -1
}

func Test_coinChange(t *testing.T) {
	fmt.Println(coinChange([]int{1, 2, 5}, 11))
	fmt.Println(coinChange([]int{2}, 3))
	fmt.Println(coinChange([]int{1}, 0))
	fmt.Println(coinChange([]int{474, 83, 404, 3}, 264))
}
