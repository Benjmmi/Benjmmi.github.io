package sample

func splitArray(nums []int) int {

	return 0
}

// 322223 7 222 5
// 3 22237222 5

func MaxComDivisor(x, y int) int {
	for true {
		if x > y {
			x -= y
		} else if x < y {
			y -= x
		} else {
			return x
		}
	}
	return 1
}
