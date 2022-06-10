package sample

import (
	"fmt"
	"testing"
)

func Test_findKthLargest(t *testing.T) {
	fmt.Println(findKthLargest([]int{3, 2, 1, 5, 6, 4}, 2))
	fmt.Println(findKthLargest([]int{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4))
	fmt.Println(findKthLargest([]int{3}, 1))
	fmt.Println(findKthLargest([]int{3, 2}, 1))
	fmt.Println(findKthLargest([]int{3, 2}, 2))
	fmt.Println(findKthLargest([]int{3, 2, 1, 0}, 4))
}
