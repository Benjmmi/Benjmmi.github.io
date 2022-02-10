package sample

import (
	"fmt"
	"testing"
)

func Test_largestDivisibleSubset(t *testing.T) {
	fmt.Println(largestDivisibleSubset([]int{3, 17}))
	fmt.Println(largestDivisibleSubset([]int{1, 3}))
	fmt.Println(largestDivisibleSubset([]int{1, 2, 3}))
	fmt.Println(largestDivisibleSubset([]int{1, 2, 4, 8, 16}))
}
