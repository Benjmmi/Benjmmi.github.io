package sample

import (
	"fmt"
	"testing"
)

func Test_findIntegers(t *testing.T) {
	// 13
	// 8 + 2 + 1
	// 1111 1110 1101 1100 1011 1010 1001 1000 111 110 101 100 11 10 1
	// 8 + 4 + 2 + 1
	// 1000
	// 100
	// 10
	// 1
	fmt.Println(findIntegers(15))
	fmt.Println((8<<1 - 1) - (8 | 8>>1))
	fmt.Println((4<<1 - 1) - (4))
	fmt.Println((2<<1 - 1) - (2))
	fmt.Println((1<<1 - 1) - (1))
}
