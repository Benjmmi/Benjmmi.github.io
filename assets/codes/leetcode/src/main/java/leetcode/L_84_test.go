package sample

import (
	"fmt"
	"testing"
)

func Test_largestRectangleArea(t *testing.T) {
	fmt.Println(largestRectangleArea([]int{2}))
	fmt.Println(largestRectangleArea([]int{2, 4}))
	fmt.Println(largestRectangleArea([]int{2, 1, 2}))
	fmt.Println(largestRectangleArea([]int{5, 4, 1, 2}))
	fmt.Println(largestRectangleArea([]int{4, 2, 0, 3, 2, 5}))
	fmt.Println(largestRectangleArea([]int{2, 1, 2, 3, 5, 6}))
	fmt.Println(largestRectangleArea([]int{2, 1, 5, 6, 2, 3}))
	fmt.Println(largestRectangleArea([]int{2, 1, 5, 15, 2, 3}))
	fmt.Println(largestRectangleArea([]int{3, 6, 5, 7, 4, 8, 1, 0}))
}
