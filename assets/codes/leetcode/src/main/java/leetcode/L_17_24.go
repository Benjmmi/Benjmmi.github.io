package sample

import "math"

func getMaxMatrix(matrix [][]int) []int {
	ans := make([]int, 4)
	N := len(matrix)
	M := len(matrix[0])
	max := math.MinInt

	for i := 0; i < N; i++ {
		b := make([]int, M)
		bestr1 := 0
		bestc1 := 0
		for j := i; j < N; j++ {
			sum := 0
			for k := 0; k < M; k++ {
				b[k] += matrix[j][k]
				if sum > 0 {
					sum += b[k]
				} else {
					sum = b[k]
					bestr1 = i
					bestc1 = k
				}
				if sum > max {
					max = sum
					ans[0] = bestr1
					ans[1] = bestc1
					ans[2] = j
					ans[3] = k
				}
			}
		}
	}

	return ans
}
