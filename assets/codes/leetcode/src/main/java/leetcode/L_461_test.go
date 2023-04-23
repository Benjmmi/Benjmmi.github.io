package sample

import (
	"fmt"
	"testing"
)

func Test_hammingDistance(t *testing.T) {
	fmt.Println(hammingDistance(1, 4))
	fmt.Println(hammingDistance(16, 8))
	fmt.Println(hammingDistance(15, 8))
	fmt.Println(hammingDistance(3, 1))
}
