package sample

func lengthOfLongestSubstringTwoDistinct(s string) int {
	if len(s) <= 2 {
		return len(s)
	}

	max := 0
	hash := map[byte]int{}
	right := 0
	left := 0
	for right < len(s) {

		if len(hash) <= 2 {
			hash[s[right]] = right
			right++
		}
		if len(hash) == 3 {
			k, v := findMin(hash)
			delete(hash, k)
			left = v + 1
		}
		if right-left > max {
			max = right - left
		}
	}
	return max
}

func findMin(kv map[byte]int) (byte, int) {
	var key byte
	min := 9999
	for k, v := range kv {
		if v < min {
			min = v
			key = k
		}
	}
	return key, min
}

//type kv struct {
//	c   byte
//	num int
//}
//func lengthOfLongestSubstringTwoDistinct(s string) int {
//	if len(s) <= 1 {
//		return len(s)
//	}
//	windows := []kv{{s[0], 0}, {s[0], 0}}
//	markC := 0
//	max := 0
//	for i := 0; i < len(s); i++ {
//		if s[i] == windows[0].c {
//			windows[0].num++
//		} else if s[i] == windows[1].c {
//			windows[1].num++
//		} else {
//			windows[0] = kv{c: s[markC], num: i - markC}
//			windows[1] = kv{c: s[i], num: 1}
//		}
//		if windows[0].num+windows[1].num > max {
//			max = windows[0].num + windows[1].num
//		}
//		if s[i] != s[markC] {
//			markC = i
//		}
//	}
//
//	return max
//}

//type kv struct {
//	c   byte
//	num int
//}
//func lengthOfLongestSubstringTwoDistinct(s string) int {
//	if len(s) <= 1 {
//		return len(s)
//	}
//	windows := []kv{{s[0], 0}, {}}
//	max := 0
//	for i := 0; i < len(s); i++ {
//		if windows[0].c == s[i] {
//			windows[0].num++
//		} else if windows[1].c == 0 {
//			windows[1].c = s[i]
//			windows[1].num = 0
//			i--
//			continue
//		} else if windows[1].c == s[i] {
//			windows[1].num++
//		} else {
//			if windows[0].c == s[i-1] {
//				j := 0
//				for ; j > 0 && s[i-j] == s[i-1]; j++ {
//
//				}
//				i = i - j - 1
//				windows[1] = kv{0, 0}
//			} else if windows[1].c == s[i-1] {
//				j := 0
//				for ; j > 0 && s[i-j] == s[i-1]; j++ {
//
//				}
//				i = i - j - 1
//				windows[0] = kv{windows[1].c, windows[1].num}
//				windows[1] = kv{0, 0}
//			}
//
//		}
//		if windows[0].num+windows[1].num > max {
//			max = windows[0].num + windows[1].num
//		}
//	}
//
//	return max
//}
