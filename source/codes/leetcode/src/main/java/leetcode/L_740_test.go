package sample

import (
	"fmt"
	"testing"
)

func Test_deleteAndEarn(t *testing.T) {
	fmt.Println(deleteAndEarn([]int{3, 4, 2}))
	fmt.Println(deleteAndEarn([]int{2, 2, 3, 3, 3, 4}))
}
