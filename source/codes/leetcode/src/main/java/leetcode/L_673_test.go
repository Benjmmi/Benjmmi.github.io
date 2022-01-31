package sample

import (
	"fmt"
	"testing"
)

func Test_findNumberOfLIS(t *testing.T) {
	fmt.Println(findNumberOfLIS([]int{1, 2}))
	fmt.Println(findNumberOfLIS([]int{1, 3, 5, 4, 7}))
	fmt.Println(findNumberOfLIS([]int{1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3}))
	fmt.Println(findNumberOfLIS([]int{1, 1, 1, 4, 4, 4, 4, 4, 2, 2, 2, 5, 5}))
	fmt.Println(findNumberOfLIS([]int{2, 2, 2, 2, 2}))
	fmt.Println(findNumberOfLIS([]int{1, 2, 4, 3, 5, 4, 7, 2}))
}
