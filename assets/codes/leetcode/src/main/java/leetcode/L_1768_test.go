package sample

import (
	"fmt"
	"testing"
)

func Test_mergeAlternately(t *testing.T) {
	fmt.Println(mergeAlternately("word1", "word2") == "wwoorrdd12")
	fmt.Println(mergeAlternately("abc", "pqr") == "apbqcr")
	fmt.Println(mergeAlternately("ab", "pqrs") == "apbqrs")
	fmt.Println(mergeAlternately("abcd", "pq") == "apbqcd")
}
