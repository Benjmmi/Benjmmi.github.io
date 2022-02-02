package sample

import (
	"fmt"
	"testing"
)

func Test_Constructor(t *testing.T) {
	obj := Constructor([][]int{{-4, -5}})
	fmt.Println(obj.SumRegion(0, 0, 0, 0))
	fmt.Println(obj.SumRegion(0, 0, 0, 1))
	fmt.Println(obj.SumRegion(0, 1, 0, 1))
}
