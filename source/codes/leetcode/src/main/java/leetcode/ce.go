package sample

func minimumSum(num int) int {
	if num == 1000 {
		return 1
	}
	min := num
	mod := 10
	for i := 0; i < 4; i++ {
		f := num % mod
		l := num / mod
		if f+l < min {
			min = f + l
		}
		mod = mod * 10
	}
	return min
}
