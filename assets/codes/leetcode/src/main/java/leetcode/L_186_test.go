package sample

import (
	"fmt"
	"testing"
)

func Test_reverseWords(t *testing.T) {
	bs := []byte{'t', 'h', 'e', ' ', 's', 'k', 'y', ' ', 'i', 's', ' ', 'b', 'l', 'u', 'e'}
	reverseWords(bs)
	fmt.Println(string(bs))
	bs = []byte{'t', 'h', 'e'}
	reverseWords(bs)
	fmt.Println(string(bs))
}
