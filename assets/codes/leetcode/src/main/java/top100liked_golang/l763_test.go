package top100liked_golang

import (
	"fmt"
	"testing"
)

//func partitionLabels(s string) []int {
//	left, right := [26]int{-1}, [26]int{-1}
//	n := len(s)
//	for i := 0; i < 26; i++ {
//		left[i] = n + 1
//		right[i] = -1
//	}
//	for index, ele := range s {
//		left[ele-'a'] = min(left[ele-'a'], index)
//		right[ele-'a'] = max(right[ele-'a'], index)
//	}
//
//	for i := 0; i < 26; i++ {
//		l, r := left[i], right[i]
//		for j := 0; j < 26; j++ {
//			ll, rr := left[j], right[j]
//			if (l >= ll && l <= rr) || (r >= ll && r <= rr) {
//				left[j], right[j] = min(left[i], right[i], left[j], right[j]), max(left[i], right[i], left[j], right[j])
//				left[i], right[i] = left[j], right[j]
//				l, r = left[i], right[i]
//			}
//		}
//	}
//
//	maps := make(map[int]int)
//	ks := []int{}
//	for i := range left {
//		if left[i] < 0 || left[i] >= n {
//			continue
//		}
//		if _, ok := maps[left[i]]; ok {
//			continue
//		}
//		ks = append(ks, left[i])
//		maps[left[i]] = right[i]
//	}
//
//	sort.Ints(ks)
//	result := make([]int, 0)
//	for _, key := range ks {
//		result = append(result, maps[key]-key+1)
//	}
//
//	// 区间合并
//	return result
//}

func partitionLabels(s string) []int {
	lastPos := [26]int{}

	for i, ele := range s {
		lastPos[ele-'a'] = i
	}
	ans := []int{}
	start, end := 0, 0
	for i, ele := range s {
		if lastPos[ele-'a'] > end {
			end = lastPos[ele-'a']
		}
		if i == end {
			ans = append(ans, end-start+1)
			start = end + 1
		}
	}
	return ans
}

func Test_partitionLabels(t *testing.T) {
	fmt.Println(partitionLabels("ababcbacadefegdehijhklij"))
	fmt.Println(partitionLabels("abc"))
}
