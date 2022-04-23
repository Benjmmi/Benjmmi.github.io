package sample

import (
	"fmt"
	"testing"
)

func Test_longestSubarray(t *testing.T) {
	fmt.Println(longestSubarray([]int{8, 2, 4, 7}, 4))
	fmt.Println(longestSubarray([]int{10, 1, 2, 4, 7, 2}, 5))
}
