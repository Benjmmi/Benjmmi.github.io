package sample

import (
	"fmt"
	"testing"
)

func Test_maxProfit3(t *testing.T) {
	fmt.Println(maxProfit3([]int{3, 3, 5, 0, 0, 3, 1, 4}))
	fmt.Println(maxProfit3([]int{1, 2, 3, 4, 5}))
	fmt.Println(maxProfit3([]int{7, 6, 4, 3, 1}))
	fmt.Println(maxProfit3([]int{1}))
}
