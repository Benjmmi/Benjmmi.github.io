package sample

import (
	"fmt"
	"testing"
)

func Test_pivotIndex(t *testing.T) {
	fmt.Println(pivotIndex([]int{1, 7, 3, 6, 5, 6}))
	fmt.Println(pivotIndex([]int{16}))
	fmt.Println(pivotIndex([]int{1, 6}))
	fmt.Println(pivotIndex([]int{1, 6, 1}))
	fmt.Println(pivotIndex([]int{1, 6, 6, 1}))
	fmt.Println(pivotIndex([]int{2, 1, -1}))
}
