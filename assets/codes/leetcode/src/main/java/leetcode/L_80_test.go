package sample

import (
	"fmt"
	"testing"
)

func Test_removeDuplicates801(t *testing.T) {
	nums := []int{1, 1, 1, 2, 2, 3}
	fmt.Println(removeDuplicates80(nums))
	fmt.Println(nums)
}

func Test_removeDuplicates802(t *testing.T) {
	nums := []int{0, 0, 1, 1, 1, 1, 2, 3, 3}
	fmt.Println(removeDuplicates80(nums))
	fmt.Println(nums)
}

func Test_removeDuplicates803(t *testing.T) {
	nums := []int{0, 1, 2, 2, 3, 4, 5, 5, 6}
	fmt.Println(removeDuplicates80(nums))
	fmt.Println(nums)
}
