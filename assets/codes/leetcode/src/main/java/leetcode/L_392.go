package sample

func isSubsequence(s string, t string) bool {
	mark := 0
	num := 0
	i := mark
	for sc := range s {
		for i < len(t) {
			if t[i] == s[sc] {
				i++
				num++
				break
			}
			i++
		}
	}
	if num == len(s) {
		return true
	}
	return false
}
