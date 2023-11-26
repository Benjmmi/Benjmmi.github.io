package sample

func findMaximumXOR(nums []int) int {
	hitBit := 30
	x := 0
	for k := hitBit; k >= 0; k-- {
		seen := map[int]bool{}
		for _, num := range nums {
			seen[num>>k] = true
		}

		xNext := x*2 + 1
		found := false
		for _, num := range nums {
			if seen[num>>k^xNext] {
				found = true
				break
			}
		}

		if found {
			x = xNext
		} else {
			x = xNext - 1
		}
	}
	return x
}

//
//func findMaximumXOR(nums []int) int {
//	max := 0
//	m := map[int]int{}
//	arr := []int{}
//	for _, num := range nums {
//		if _, exit := m[num]; exit {
//			continue
//		}
//		m[num] = 0
//		arr = append(arr, num)
//	}
//	length := len(arr)
//	for i := 0; i < length; i++ {
//		for j := i + 1; j < length; j++ {
//			max = int(math.Max(float64(max), float64(arr[i]^arr[j])))
//		}
//	}
//	return max
//}
