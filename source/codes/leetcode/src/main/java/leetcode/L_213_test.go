package sample

import (
	"fmt"
	"testing"
)

func Test_rob2(t *testing.T) {
	fmt.Println(rob([]int{1, 2, 1, 1}))
	fmt.Println(rob([]int{1, 1, 3, 6, 1, 1, 3}))
	fmt.Println(rob([]int{1, 1, 3, 6, 7, 10, 7, 1, 8, 5, 9, 1, 4, 4, 3}))
}
