package sample

import (
	"fmt"
	"testing"
)

func Test_getMaxMatrix(t *testing.T) {
	fmt.Println(getMaxMatrix([][]int{{-9, -8, -1, -3, -2}, {-3, -7, -6, -2, -4}, {-6, -4, -4, -8, -7}}))
}
