package sample

func combine(n int, k int) [][]int {
	r := new([][]int)
	for i := 1; i <= n; i++ {
		line := make([]int, k)
		line[0] = i
		combine2(i, n, 1, k, line, r)
	}
	return *r
}

func combine2(cur, n, curIndex, k int, line []int, r *[][]int) {
	if curIndex == k {
		cc := make([]int, k)
		copy(cc, line)
		*r = append(*r, cc)
		return
	}
	for i := cur; i <= n; i++ {
		line[curIndex] = i
		combine2(i, n, curIndex+1, k, line, r)
	}
}
