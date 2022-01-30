package sample

func maxArea(height []int) int {
	start := 0
	end := len(height) - 1
	maxValue := 0

	for start < end {
		var value int
		if height[start] < height[end] {
			value = height[start] * (end - start)
			start++
		} else {
			value = height[end] * (end - start)
			end--
		}
		if value > maxValue {
			maxValue = value
		}
	}

	return maxValue
}
