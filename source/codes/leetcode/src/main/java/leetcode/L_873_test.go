package sample

import (
	"fmt"
	"testing"
)

func Test_lenLongestFibSubseq(t *testing.T) {
	fmt.Println(lenLongestFibSubseq([]int{1, 2, 3, 4, 5, 6, 7, 8}))
	fmt.Println(lenLongestFibSubseq([]int{1, 3, 7, 11, 12, 14, 18}))
	fmt.Println(lenLongestFibSubseq([]int{1, 3, 5}))
}
