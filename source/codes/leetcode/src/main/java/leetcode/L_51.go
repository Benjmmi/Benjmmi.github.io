package sample

func solveNQueens(n int) [][]string {
	for i := 0; i < n; i++ {
		queuing := make([][]string, n)
		j := 0
		s := make([]string, n)
		s[i] = "Q"
		queuing = append(queuing, s)
		if issolveNQueens(&queuing, n, j+1) {
			return queuing
		}
	}
	return nil
}

func issolveNQueens(q *[][]string, n int, j int) bool {
	if !(j < n) {
		return true
	}
	for i := 0; i < n; i++ {
		s := make([]string, n)
		s[i] = "Q"
		*q = append(*q, s)
		if issolveNQueens(q, n, j+1) {
			return true
		}
	}
	return false
}
func ll() {

}

func lr() {

}

func up() {

}

func upl() {

}

func upr() {

}
