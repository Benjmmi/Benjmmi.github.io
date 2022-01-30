package sample

import (
	"fmt"
	"testing"
)

func Test_lengthOfLongestSubstringTwoDistinct(t *testing.T) {
	fmt.Println(lengthOfLongestSubstringTwoDistinct("a"))
	fmt.Println(lengthOfLongestSubstringTwoDistinct("eceba"))
	fmt.Println(lengthOfLongestSubstringTwoDistinct("ccaabbbc"))
	fmt.Println(lengthOfLongestSubstringTwoDistinct("abcdefghijklm"))
	fmt.Println(lengthOfLongestSubstringTwoDistinct("abbbbbbbbbbbc"))
	fmt.Println(lengthOfLongestSubstringTwoDistinct("abbbbbbbbbbba"))
	fmt.Println(lengthOfLongestSubstringTwoDistinct("abbbbbbbbbbbcd"))
	fmt.Println(lengthOfLongestSubstringTwoDistinct("abababaababababbcacacaca"))
}
