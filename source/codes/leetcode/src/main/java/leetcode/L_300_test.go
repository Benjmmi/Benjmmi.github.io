package sample

import (
	"fmt"
	"testing"
)

func Test_lengthOfLIS(t *testing.T) {
	fmt.Println(lengthOfLIS([]int{0, 3, 1, 6, 2, 2, 7}))
	fmt.Println(lengthOfLIS([]int{10, 9, 2, 5, 3, 7, 101, 18}))
	fmt.Println(lengthOfLIS([]int{0, 1, 0, 3, 2, 3}))
	fmt.Println(lengthOfLIS([]int{7, 7, 7, 7, 7, 7, 7}))
	fmt.Println(lengthOfLIS([]int{0, 1}))
	fmt.Println(lengthOfLIS([]int{3, 1}))
	fmt.Println(lengthOfLIS([]int{3}))
}
