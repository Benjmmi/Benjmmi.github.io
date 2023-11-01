package sample

import "strings"

func truncateSentence(s string, k int) string {
	if len(s) == 0 {
		return s
	}
	ss := strings.Split(s, " ")
	a := ss[:k]
	return strings.Join(a, " ")
}
