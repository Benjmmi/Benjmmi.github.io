package sample

import (
	"fmt"
	"testing"
)

func Test_generateParenthesis(t *testing.T) {
	fmt.Println(generateParenthesis(1))
	fmt.Println(generateParenthesis(2))
	fmt.Println(generateParenthesis(3))
	fmt.Println(generateParenthesis(4))
	fmt.Println(generateParenthesis(5))
	fmt.Println(generateParenthesis(6))
	fmt.Println(generateParenthesis(7))
	fmt.Println(generateParenthesis(8))
	fmt.Println(generateParenthesis(9))
	fmt.Println(generateParenthesis(10))
	a := "abcd"
	a = a[:len(a)] + "e"
	fmt.Println(a)
}
