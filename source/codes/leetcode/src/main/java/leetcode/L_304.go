package sample

type NumMatrix struct {
	sums [][]int
}

func Constructor1(matrix [][]int) NumMatrix {
	N := len(matrix)
	M := len(matrix[0])
	var sumMatrix = NumMatrix{sums: make([][]int, N)}
	for i := 0; i < N; i++ {
		sumMatrix.sums[i] = make([]int, M)
	}
	for i := 0; i < N; i++ {
		for j := 0; j < M; j++ {
			sumMatrix.sums[i][j] = sumMatrix.point(i-1, j) + sumMatrix.point(i, j-1) - sumMatrix.point(i-1, j-1) + matrix[i][j]
		}
	}
	return sumMatrix
}

func (this *NumMatrix) SumRegion(row1 int, col1 int, row2 int, col2 int) int {

	return this.point(row2, col2) - this.point(row2, col1-1) - this.point(row1-1, col2) + this.point(row1-1, col1-1)
}

func (this *NumMatrix) point(x, y int) int {
	if x < 0 || y < 0 {
		return 0
	}
	return this.sums[x][y]
}
