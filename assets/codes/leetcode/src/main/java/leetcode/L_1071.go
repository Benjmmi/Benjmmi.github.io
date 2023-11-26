package sample

import "strings"

func gcdOfStrings(str1 string, str2 string) string {
	max := ""
	min := ""
	if len(str1) > len(str2) {
		max = str1
		min = str2
	} else {
		max = str2
		min = str1
	}
	str1, str2 = max, min
	s := strings.ReplaceAll(str1, str2, "")
	if len(s) == 0 {
		return str2
	}
	for i := len(str2)/2 + 1; i > 0; i-- {
		if len(str1)%i != 0 {
			continue
		}
		old := str2[:i]
		s1 := strings.ReplaceAll(str1, old, "")
		s2 := strings.ReplaceAll(str2, old, "")
		if len(s1) == 0 && len(s2) == 0 {
			return str2[:i]
		}
	}
	return ""
}
