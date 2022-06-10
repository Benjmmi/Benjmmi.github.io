package sample

import (
	"fmt"
	"testing"
)

func Test_maxSumSubmatrix(t *testing.T) {
	//fmt.Println(maxSumSubmatrix([][]int{{2, 2, -1}}, 3))
	//fmt.Println(maxSumSubmatrix([][]int{{1, 0, 1}, {0, -2, 3}}, 2))
	//fmt.Println(ceiling([]int{1, 3}, 2))
	fmt.Println(ceiling([]int{-2, 0, 0}, -1))
	fmt.Println(ceiling([]int{-2, 0, 0}, -3))
	fmt.Println(ceiling([]int{1, 3, 0}, 2))
	fmt.Println(ceiling([]int{1, 3, 0}, 4))
}
