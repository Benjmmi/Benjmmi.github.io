package sample

import (
	"fmt"
	"testing"
)

func Test_removeDuplicates1(t *testing.T) {
	arr := []int{1, 1, 2}
	fmt.Println(removeDuplicates(arr))
	fmt.Println(arr)
}

func Test_removeDuplicates2(t *testing.T) {
	arr := []int{0, 0, 1, 1, 1, 2, 2, 3, 3, 4}
	fmt.Println(removeDuplicates(arr))
	fmt.Println(arr)
}
