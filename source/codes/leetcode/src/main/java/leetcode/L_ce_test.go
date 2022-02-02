package sample

import (
	"fmt"
	"testing"
)

func Test_canArrange(t *testing.T) {
	fmt.Println(minCostClimbingStairs([]int{10, 15, 20}))
	fmt.Println(minCostClimbingStairs([]int{1, 100, 1, 1, 1, 100, 1, 1, 100, 1}))
}
