package sample

func shortestWay(source string, target string) int {
	p, q, n, cnt := 0, 0, 0, 0
	for n < len(target) {
		cnt++
		p = q
		for i := 0; i < len(source) && q < len(target); i++ {
			if source[i] == target[q] {
				i++
				q++
			} else {
				i++
			}
		}
		if p == q {
			return -1
		}
	}
	return cnt + 1
}
