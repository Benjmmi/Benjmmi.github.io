package sample

func isValid(s string) bool {

	stack := []byte{}
	for i := 0; i < len(s); i++ {
		if s[i] == '(' || s[i] == '[' || s[i] == '{' {
			stack = append(stack, s[i])
		} else {
			if len(stack) == 0 {
				return false
			}
			if ')' == s[i] && stack[len(stack)-1] == '(' {
				stack = stack[:len(stack)-1]
				continue
			}
			if '}' == s[i] && stack[len(stack)-1] == '{' {
				stack = stack[:len(stack)-1]
				continue
			}
			if ']' == s[i] && stack[len(stack)-1] == '[' {
				stack = stack[:len(stack)-1]
				continue
			}
			return false
		}
	}
	return len(stack) == 0
}
