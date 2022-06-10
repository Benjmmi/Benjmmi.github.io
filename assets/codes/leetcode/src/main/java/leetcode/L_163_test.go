package sample

import (
	"fmt"
	"testing"
)

func Test_findMissingRanges(t *testing.T) {
	fmt.Println(findMissingRanges([]int{0, 1, 3, 50, 75}, 0, 99))
	fmt.Println(findMissingRanges([]int{0, 1, 3, 50, 75}, 0, 99))
	fmt.Println(findMissingRanges([]int{-1}, -1, -1))
}
