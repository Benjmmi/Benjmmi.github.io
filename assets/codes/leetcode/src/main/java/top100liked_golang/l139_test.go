package top100liked_golang

import (
	"fmt"
	"math"
	"slices"
	"testing"
)

func wordBreak(s string, wordDict []string) bool {
	minLen := math.MaxInt32
	maxLen := 0
	for _, ele := range wordDict {
		minLen = min(minLen, len(ele))
		maxLen = max(maxLen, len(ele))
	}

	start := []int{0}
	ans := make([]bool, len(s))
	exists := []int{}

	for len(start) > 0 {
		index := start[0]
		start = start[1:]
		for i := index; i <= len(s) && i <= index+maxLen; i++ {
			if i-index >= minLen && i-index <= maxLen {
				word := s[index:i]
				if slices.Contains(wordDict, word) {
					ans[i-1] = true
					if !slices.Contains(exists, i) {
						start = append(start, i)
						exists = append(exists, i)
					}
				}
			}
		}
	}

	return ans[len(s)-1]
}

func Test_wordBreak(t *testing.T) {
	//fmt.Println(wordBreak("leetcode", []string{"leet", "code"}))
	//fmt.Println(wordBreak("applepenapple", []string{"apple", "pen"}))
	//fmt.Println(wordBreak("catsandog", []string{"cats", "dog", "sand", "and", "cat"}))
	fmt.Println(wordBreak("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", []string{"a", "aa", "aaa", "aaaaaaa", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"}))
}
