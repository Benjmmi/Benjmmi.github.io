package sample

import (
	"fmt"
	"testing"
)

func Test_findMaximumXOR(t *testing.T) {
	arr := []int{3, 10, 5, 25, 2, 8}
	fmt.Println(findMaximumXOR(arr))
	arr = []int{14, 70, 53, 83, 49, 91, 36, 80, 92, 51, 66, 70}
	fmt.Println(findMaximumXOR(arr))
}
