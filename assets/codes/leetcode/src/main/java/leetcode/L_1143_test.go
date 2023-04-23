package sample

import (
	"fmt"
	"testing"
)

func Test_longestCommonSubsequence(t *testing.T) {
	fmt.Println(longestCommonSubsequence("weqrewqwqerrewqrqwrew", "erqwrwfdsafaswerq"))
	fmt.Println(longestCommonSubsequence("erqwrwe", "erqwrwwer"))
	fmt.Println(longestCommonSubsequence("abcde", "ace"))
	fmt.Println(longestCommonSubsequence("abc", "abc"))
	fmt.Println(longestCommonSubsequence("abc", "def"))
}
