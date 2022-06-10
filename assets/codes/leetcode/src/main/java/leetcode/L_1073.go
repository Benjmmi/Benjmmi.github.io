package sample

func addNegabinary(arr1 []int, arr2 []int) []int {
	if len(arr2) > len(arr1) {
		arr1, arr2 = arr2, arr1
	}
	a1L := len(arr1) - 1
	a2L := len(arr2) - 1
	pre := 0
	for i := 0; i < a2L; i++ {
		sum := arr1[a1L-i] + arr2[a2L-i] + pre
		arr1[a1L-i] = sum % 2
		pre = sum / 2
	}

	if pre != 0 {
		for i := a2L; i < a1L; i++ {
			sum := arr1[a1L-i] + pre
			arr1[a1L-i] = sum % 2
			pre = sum / 2
			if pre == 0 {
				break
			}
		}
	}
	if pre == 1 {
		arr1 = append([]int{pre}, arr1...)
	}
	return arr1
}
