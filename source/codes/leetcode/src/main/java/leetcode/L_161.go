package sample

import "strings"

func isOneEditDistance(s string, t string) bool {

	if len(s) < 2 && len(t) < 2 {
		if strings.Compare(s, t) == 0 {
			return false
		}
		return true
	}
	if strings.Compare(s, t) == 0 {
		return false
	}
	sb := []byte(s)
	tb := []byte(t)

	if len(s) == len(t) {
		// replace
		for i := 0; i < len(t); i++ {
			if s[i] != t[i] {
				sb[i] = tb[i]
				break
			}
		}
	} else if len(s) < len(t) {
		// add
		b := false
		for i := 0; i < len(s); i++ {
			if sb[i] != tb[i] {
				sb_end := string(sb[i:])
				sb_start := append(sb[:i], tb[i])
				sb = append(sb_start, sb_end...)
				b = true
				break
			}
		}
		if !b {
			sb = append(sb, tb[len(tb)-1])
		}
	} else {
		// remove
		b := false
		for i := 0; i < len(t); i++ {
			if sb[i] != tb[i] {
				sb = append(sb[:i], sb[i+1:]...)
				b = true
				break
			}
		}
		if !b {
			sb = sb[:len(sb)-1]
		}
	}

	s = string(sb)
	t = string(tb)
	return strings.Compare(s, t) == 0
}
