package top100liked_golang

import (
	"fmt"
	"testing"
)

func generate(numRows int) [][]int {
	ans := [][]int{}
	ans = append(ans, []int{1})
	if numRows == 1 {
		return [][]int{{1}}
	}
	ans = append(ans, []int{1, 1})
	if numRows == 2 {
		return [][]int{{1}, {1, 1}}
	}

	for i := 2; i < numRows; i++ {
		row := []int{}
		row = append(row, 1)
		for j := 1; j < i; j++ {
			row = append(row, ans[i-1][j-1]+ans[i-1][j])
		}
		row = append(row, 1)
		ans = append(ans, row)
	}
	return ans
}

func Test_generate(t *testing.T) {
	fmt.Println(generate(1))
	fmt.Println(generate(5))
}
