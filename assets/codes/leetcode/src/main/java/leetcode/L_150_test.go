package sample

import (
	"fmt"
	"testing"
)

func Test_rotate(t *testing.T) {
	arr := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
	rotate(arr, 3)
	fmt.Println(arr)
}
