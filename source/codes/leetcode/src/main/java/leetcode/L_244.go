package sample

import "math"

type WordDistance struct {
	kv map[string][]int
}

func Constructor(wordsDict []string) WordDistance {
	kv := map[string][]int{}
	for i, s := range wordsDict {
		if _, e := kv[s]; e {
			kv[s] = append(kv[s], i)
		} else {
			kv[s] = []int{i}
		}
	}
	return WordDistance{
		kv: kv,
	}
}

func (this *WordDistance) Shortest(word1 string, word2 string) int {
	length := math.MaxInt
	for i := 0; i < len(this.kv[word1]); i++ {
		for j := 0; j < len(this.kv[word2]); j++ {
			length = min(length, int(math.Abs(float64((this.kv[word1][i] - this.kv[word2][j])))))
		}
	}

	return length
}
