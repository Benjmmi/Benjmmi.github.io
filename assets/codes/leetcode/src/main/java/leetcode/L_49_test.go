package sample

import (
	"fmt"
	"testing"
)

func Test_groupAnagrams(t *testing.T) {
	var s []string
	s = []string{"eat", "tea", "tan", "ate", "nat", "bat"}
	fmt.Println(groupAnagrams(s))
	s = []string{""}
	fmt.Println(groupAnagrams(s))
	s = []string{"a"}
	fmt.Println(groupAnagrams(s))
}
