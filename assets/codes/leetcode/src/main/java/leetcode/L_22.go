package sample

func generateParenthesis(n int) []string {
	s := []string{}
	if n == 0 {
		return s
	}
	dsfParenthesis("", n, n, &s)
	return s
}

func dsfParenthesis(cur string, left, right int, result *[]string) {
	if left == 0 && right == 0 {
		bs := make([]byte, len(cur))
		copy(bs, cur)
		*result = append(*result, string(bs))
	}
	if left > right {
		return
	}
	if left > 0 {
		dsfParenthesis(cur+"(", left-1, right, result)
	}
	if right > 0 {
		dsfParenthesis(cur+")", left, right-1, result)
	}
}
