package sample

import "sort"

func canArrange(arr []int, k int) bool {
	sort.Ints(arr)
	for len(arr) > 0 {
		i := arr[0]
		arr = arr[1:]
		b := false
		for j := len(arr) - 1; j >= 0; j-- {
			if (arr[j]+i)%k == 0 {
				arr = append(arr[:j], arr[j+1:]...)
				b = true
				break
			}
		}
		if !b {
			return false
		}
	}

	return true
}
