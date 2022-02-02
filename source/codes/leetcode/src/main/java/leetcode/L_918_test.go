package sample

import (
	"fmt"
	"testing"
)

func Test_maxSubarraySumCircular(t *testing.T) {
	//fmt.Println(maxSubarraySumCircular([]int{1, 2, 3, -6, 1, 2, 3}), "=12")
	//fmt.Println(maxSubarraySumCircular([]int{1, 2, 3, -6, 1, 2, 3, -6}), "=6")
	//fmt.Println(maxSubarraySumCircular([]int{1, 2, 3, -6, 1, 2, 3, -6, 1, 2, 3}), "=12")
	//fmt.Println(maxSubarraySumCircular([]int{-7, -6, -5, -4, -3, -2, 3, 4, -4, -5}), "=7")
	//fmt.Println(maxSubarraySumCircular([]int{1, -2, 3, -2}), "=3")
	//fmt.Println(maxSubarraySumCircular([]int{-1, -2, -3, -2}), "=-1")
	//fmt.Println(maxSubarraySumCircular([]int{1, 2, 3, 4}), "=10")
	//fmt.Println(maxSubarraySumCircular([]int{5, -3, 5}), "=10")
	//fmt.Println(maxSubarraySumCircular([]int{3, -1, 2, -1}), "=4")
	//fmt.Println(maxSubarraySumCircular([]int{3, -2, 2, -3}), "=3")
	//fmt.Println(maxSubarraySumCircular([]int{-1, -2, -4, -3}), "=-1")
	//fmt.Println(maxSubarraySumCircular([]int{5, 5, 5, 5, 5, -3, 5}), "=30")
	//fmt.Println(maxSubarraySumCircular([]int{5, -3, 5, 5, 5, 5, -3, 5}), "=27")
	//fmt.Println(maxSubarraySumCircular([]int{1, 0}), "=1")
	//fmt.Println(maxSubarraySumCircular([]int{-1, 0}), "=0")
	//fmt.Println(maxSubarraySumCircular([]int{0, 0}), "=0")
	//fmt.Println(maxSubarraySumCircular([]int{-1, 3, -3, 9, -6, 8, -5, -5, -6, 10}), "=20")
	//fmt.Println(maxSubarraySumCircular([]int{3, -1, -2, -3, 10}), "=13")
	//fmt.Println(maxSubarraySumCircular([]int{-3, 1, 2, 3, -10}), "=6")
	fmt.Println(preSum([]int{3, -1, -2, -4, 10}), "=13")
	//fmt.Println(preSum([]int{-3, 1, 2, 3, -10}), "=6")
}
