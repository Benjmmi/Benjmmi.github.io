package sample

func twoSum(numbers []int, target int) []int {

	length := len(numbers)
	for i := 0; i < length; i++ {
		v := target - numbers[i]
		left := i
		right := length
		for left < right {
			mid := (left + right) / 2
			if numbers[mid] > v {
				right = mid
			} else if numbers[mid] < v {
				left = mid + 1
			} else {
				return []int{i + 1, mid + 1}
			}
		}
	}

	return nil
}
