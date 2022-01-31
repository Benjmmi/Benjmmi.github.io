package sample

import (
	"fmt"
	"testing"
)

func Test_maxSubArray(t *testing.T) {
	fmt.Println(maxSubArray([]int{-2, 1, -3, 4, -1, 2, 1, -5, 4}))
	fmt.Println(maxSubArray([]int{5, 4, -1, 7, 8}))
	fmt.Println(maxSubArray([]int{1, 2}))
	fmt.Println(maxSubArray([]int{1}))
}
