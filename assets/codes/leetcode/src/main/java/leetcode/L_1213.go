package sample

import "sort"

func arraysIntersection(arr1 []int, arr2 []int, arr3 []int) []int {
	arr1 = append(arr1, arr2...)
	arr1 = append(arr1, arr3...)
	sort.Ints(arr1)
	result := []int{}
	for i := 0; i < len(arr1)-3; i++ {
		if arr1[i] == arr1[i+1] && arr1[i] == arr1[i+2] {
			result = append(result, arr1[i])
		}
	}
	return result
}
