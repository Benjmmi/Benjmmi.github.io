package sample

import (
	"fmt"
	"testing"
)

func Test_solution(t *testing.T) {
	s := []byte{'a', 'b', 'c', 'd', 'e', 'f'}
	a := s[0:3]
	fmt.Println(string(a))
}
