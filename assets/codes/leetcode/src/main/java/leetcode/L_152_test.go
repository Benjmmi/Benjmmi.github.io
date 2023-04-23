package sample

import (
	"fmt"
	"testing"
)

func Test_maxProduct(t *testing.T) {
	fmt.Println(maxProduct([]int{2, 3, -2, 4}))
	fmt.Println(maxProduct([]int{-2, 0, -1}))
	fmt.Println(maxProduct([]int{-2, 3, -4}))
	fmt.Println(maxProduct([]int{-2, 3, -4, -5, 6, -6, 7, 8, -9, -10}))
}
