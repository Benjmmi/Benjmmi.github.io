package sample

import (
	"fmt"
	"sort"
	"testing"
)

func Test_maxEnvelopes(t *testing.T) {
	fmt.Println(maxEnvelopes([][]int{{5, 4}, {6, 4}, {6, 7}, {2, 3}}))
	fmt.Println(maxEnvelopes([][]int{{1, 1}, {1, 1}, {1, 1}, {1, 1}}))
	fmt.Println(maxEnvelopes([][]int{{4, 5}, {4, 6}, {6, 7}, {2, 3}, {1, 1}}))
	fmt.Println(maxEnvelopes([][]int{{1, 1}}))
	a := []int{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
	fmt.Println(sort.SearchInts(a, 9))
}
