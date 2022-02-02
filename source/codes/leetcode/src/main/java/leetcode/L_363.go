package sample

import (
	"math"
	"sort"
)

func maxSumSubmatrix(matrix [][]int, k int) int {
	N := len(matrix)
	M := len(matrix[0])
	max := math.MinInt
	sum := make([][]int, N+1)
	for i := range sum {
		sum[i] = make([]int, M+1)
	}

	for i := 1; i <= N; i++ {
		for j := 1; j <= M; j++ {
			sum[i][j] = sum[i-1][j] + sum[i][j-1] - sum[i-1][j-1] + matrix[i-1][j-1]
		}
	}

	for top := 1; top <= N; top++ {
		for bottom := top; bottom <= N; bottom++ {
			set := []int{}
			set = append(set, 0)
			for right := 1; right <= M; right++ {
				rightVal := sum[bottom][right] - sum[top-1][right]
				left := ceiling(set, rightVal-k)
				if left != -1 {
					cur := rightVal - left
					max = int(math.Max(float64(max), float64(cur)))
				}
				set = append(set, rightVal)
			}
		}
	}

	return max
}

func ceiling(nums []int, num int) int {
	sort.Ints(nums)
	i := 0
	for ; i < len(nums) && nums[i] < num; i++ {
		if nums[i] == num {
			return num
		}
	}
	if i == len(nums) {
		return -1
	}
	return nums[i]
}
