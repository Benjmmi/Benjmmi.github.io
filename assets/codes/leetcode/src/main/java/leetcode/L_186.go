package sample

func reverseWords(s []byte) {
	length := len(s)
	var result []byte
	var word []byte
	for i := 0; i < length; i++ {
		if s[i] != ' ' {
			word = append(word, s[i])
		} else {
			word = append(word, ' ')
			result = append(word, result...)
			word = []byte{}
		}
	}
	word = append(word, ' ')
	result = append(word, result...)
	for i := 0; i < len(s); i++ {
		s[i] = result[i]
	}
}
