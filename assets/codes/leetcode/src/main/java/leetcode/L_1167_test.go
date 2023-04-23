package sample

import (
	"fmt"
	"testing"
)

func Test_connectSticks(t *testing.T) {
	stick := []int{2, 4, 3}
	fmt.Println(stick[2:])
	fmt.Println(connectSticks([]int{2, 4, 3}))
	fmt.Println(connectSticks([]int{1, 8, 3, 5}))
	fmt.Println(connectSticks([]int{5}))
	fmt.Println(connectSticks([]int{3354, 4316, 3259, 4904, 4598, 474, 3166, 6322, 8080, 9009}))
}
