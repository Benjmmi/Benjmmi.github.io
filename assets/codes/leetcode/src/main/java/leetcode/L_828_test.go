package sample

import (
	"fmt"
	"testing"
)

func Test_uniqueLetterString(t *testing.T) {
	fmt.Println(uniqueLetterString("ABC"))
	fmt.Println(uniqueLetterString("ABA"))
	fmt.Println(uniqueLetterString("LEETCODE"))
}
