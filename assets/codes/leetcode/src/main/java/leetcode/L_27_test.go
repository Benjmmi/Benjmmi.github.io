package sample

import (
	"fmt"
	"testing"
)

func Test_removeElement1(t *testing.T) {
	nums := []int{3, 2, 2, 3}
	fmt.Println(removeElement(nums, 3))
	fmt.Println(nums)
}

func Test_removeElement3(t *testing.T) {
	nums := []int{0, 1, 2, 2, 3, 0, 4, 2}
	fmt.Println(removeElement(nums, 2))
	fmt.Println(nums)
}
