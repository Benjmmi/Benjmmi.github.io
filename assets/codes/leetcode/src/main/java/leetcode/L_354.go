package sample

import (
	"sort"
)

func maxEnvelopes(envelopes [][]int) int {
	if len(envelopes) <= 1 {
		return len(envelopes)
	}
	sort.Slice(envelopes, func(i, j int) bool {
		a, b := envelopes[i], envelopes[j]
		return a[0] < b[0] || a[0] == b[0] && a[1] > b[1]
	})
	count := []int{}

	for i := 0; i < len(envelopes); i++ {
		a := envelopes[i][1]
		if index := sort.SearchInts(count, a); index < len(count) {
			count[index] = a
		} else {
			count = append(count, a)
		}
	}

	return len(count)
}

//func maxEnvelopes(envelopes [][]int) int {
//	if len(envelopes) <= 1 {
//		return len(envelopes)
//	}
//	sort.Slice(envelopes, func(i, j int) bool {
//		a, b := envelopes[i], envelopes[j]
//		return a[0] < b[0] || a[0] == b[0] && a[1] > b[1]
//	})
//	count := make([]int, len(envelopes))
//	for i := range count {
//		count[i] = 1
//	}
//	max := 1
//	for i := 0; i < len(envelopes); i++ {
//		for j := 0; j < i; j++ {
//			if envelopes[i][1] > envelopes[j][1] {
//				if count[j]+1 > count[i] {
//					count[i] = count[j] + 1
//				}
//			}
//		}
//		if count[i] > max {
//			max = count[i]
//		}
//	}
//
//	return max
//}
