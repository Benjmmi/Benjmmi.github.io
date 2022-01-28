package sample

import (
	"fmt"
	"testing"
)

func Test_canArrange(t *testing.T) {
	fmt.Println(canArrange([]int{1, 2, 3, 4, 5, 6}, 7))
	fmt.Println(canArrange([]int{1, 2, 3, 4, 5, 10, 6, 7, 8, 9}, 5))
}
