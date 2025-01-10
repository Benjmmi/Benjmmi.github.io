package sample

import (
	"fmt"
	"testing"
)

func Test_majorityElement(t *testing.T) {
	arr := []int{1, 6, 2, 2, 2, 2, 7}
	fmt.Println(majorityElement(arr))
}

func Test_majorityElement1(t *testing.T) {
	arr := []int{2, 3, 2}
	fmt.Println(majorityElement(arr))
}

func Test_majorityElement2(t *testing.T) {
	arr := []int{3, 2, 3}
	fmt.Println(majorityElement(arr))
}

func Test_majorityElement3(t *testing.T) {
	arr := []int{2, 2, 1, 1, 1, 2, 2}
	fmt.Println(majorityElement(arr))
	fmt.Println(arr)
}
