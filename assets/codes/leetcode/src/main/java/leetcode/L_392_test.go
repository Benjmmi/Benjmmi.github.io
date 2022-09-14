package sample

import (
	"fmt"
	"testing"
)

func Test_isSubsequence(t *testing.T) {
	fmt.Println(isSubsequence("abc", "ahbgdc"))
	fmt.Println(isSubsequence("axc", "ahbgdc"))
	fmt.Println(isSubsequence("", "ahbgdc"))
	fmt.Println(isSubsequence("aaaaaa", "bbaaaa"))
}
