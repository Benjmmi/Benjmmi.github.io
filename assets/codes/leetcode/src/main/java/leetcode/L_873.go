package sample

import "fmt"

func lenLongestFibSubseq(arr []int) int {

	hash := map[int]int{}
	maxLen := 0
	hashkv := map[string]int{}
	for i := 0; i < len(arr); i++ {
		hash[arr[i]] = 0
	}
	for i := 0; i < len(arr); i++ {
		for j := i + 1; j < len(arr); j++ {
			rootnum := 0
			key1 := arr[i]
			key2 := arr[j]
			key := key1 + key2
			for {
				if _, es := hash[key]; es && rootnum >= hashkv[fmt.Sprintf("%d_%d", key1, key2)] {
					key1 = key2
					key2 = key
					hashkv[fmt.Sprintf("%d_%d", key1, key2)] = rootnum
					key = key1 + key2
					rootnum++
				} else {
					break
				}
			}
			if rootnum > maxLen {
				maxLen = rootnum
			}
		}
	}
	if maxLen > 0 {
		return maxLen + 2
	}
	return maxLen
}
