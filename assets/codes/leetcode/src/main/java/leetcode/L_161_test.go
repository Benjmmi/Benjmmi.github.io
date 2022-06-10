package sample

import (
	"fmt"
	"testing"
)

func Test_isOneEditDistance(t *testing.T) {
	//fmt.Println(isOneEditDistance("", ""))
	//fmt.Println(isOneEditDistance("a", ""))
	//fmt.Println(isOneEditDistance("a", "a"))
	//fmt.Println(isOneEditDistance("a", "ac"))
	//fmt.Println(isOneEditDistance("a", "A"))
	fmt.Println(isOneEditDistance("ab", "acb"))
	//fmt.Println(isOneEditDistance("ab", "acb"))
	//fmt.Println(isOneEditDistance("cab", "ab"))
	//fmt.Println(isOneEditDistance("cab", "vab"))
	//fmt.Println(isOneEditDistance("1203", "1203"))
	//fmt.Println(isOneEditDistance("12034", "1203"))
}
