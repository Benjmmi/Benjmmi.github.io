package sample

import (
	"fmt"
	"testing"
)

func Test_permute(t *testing.T) {
	fmt.Println(permute([]int{1, 2, 3}))
	//fmt.Println(permute([]int{1, 2}))
	//fmt.Println(permute([]int{1}))
	fmt.Println(permute([]int{0, -1, 1}))
}
