package sample

import (
	"fmt"
	"testing"
)

func Test_merge1(t *testing.T) {
	nums1 := []int{1}
	nums2 := []int{}
	merge(nums1, 1, nums2, 0)
	for _, ele := range nums1 {
		fmt.Println(ele)
	}
}

func Test_merge2(t *testing.T) {
	nums1 := []int{0}
	nums2 := []int{1}
	merge(nums1, 0, nums2, 1)
	for _, ele := range nums1 {
		fmt.Println(ele)
	}
}

func Test_merge3(t *testing.T) {
	nums1 := []int{1, 2, 3, 0, 0, 0}
	nums2 := []int{2, 5, 6}
	merge(nums1, 3, nums2, 3)
	fmt.Println(nums1)
}
