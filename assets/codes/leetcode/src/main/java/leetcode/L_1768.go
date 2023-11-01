package sample

func mergeAlternately(word1 string, word2 string) string {
	word := make([]byte, 0, len(word1)+len(word2))
	i := 0
	for ; i < len(word1) && i < len(word2); i++ {
		word = append(word, word1[i], word2[i])
	}
	if i < len(word1) {
		return string(word) + word1[i:]
	}
	if i < len(word2) {
		return string(word) + word2[i:]
	}
	return string(word)
}
