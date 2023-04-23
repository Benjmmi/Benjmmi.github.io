package sample

import (
	"fmt"
	"testing"
)

func Test_maxArea(t *testing.T) {
	fmt.Println(maxArea([]int{1, 8, 6, 2, 5, 4, 8, 3, 7}))
	fmt.Println(maxArea([]int{4, 3, 2, 1, 4}))
	fmt.Println(maxArea([]int{1, 1}))
	fmt.Println(maxArea([]int{1, 2, 1}))
	fmt.Println(maxArea([]int{2, 1, 3, 1, 1, 1, 1, 3}))
	fmt.Println(maxArea([]int{2, 1, 3, 33, 1, 35}))
	fmt.Println(maxArea([]int{2, 1, 3, 33, 39, 35}))
	fmt.Println(maxArea([]int{2, 1, 3, 33, 100, 101}))
	fmt.Println(maxArea([]int{0, 0, 0, 1}))
	fmt.Println(maxArea([]int{1, 2, 3, 4, 5, 6, 7, 8, 9}))
	fmt.Println(maxArea([]int{9, 8, 7, 6, 5, 4, 3, 2, 1}))
}
