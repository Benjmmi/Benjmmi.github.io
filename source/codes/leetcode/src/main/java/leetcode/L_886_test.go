package sample

import (
	"fmt"
	"testing"
)

func Test_a(t *testing.T) {

	//fmt.Println(possibleBipartition(4, [][]int{{1, 2}, {1, 3}, {2, 4}}))
	fmt.Println(possibleBipartition(3, [][]int{{1, 2}, {1, 3}, {2, 3}}))
	// 1 0      2 3 1
	// 2 1      1 0
}
