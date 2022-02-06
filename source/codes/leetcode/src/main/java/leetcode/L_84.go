package sample

func largestRectangleArea(heights []int) int {
	left := make([]int, len(heights))
	right := make([]int, len(heights))
	stack := []int{}
	for i := 0; i < len(heights); i++ {
		for len(stack) > 0 && heights[stack[len(stack)-1]] >= heights[i] {
			stack = stack[:len(stack)-1]
		}
		if len(stack) == 0 {
			left[i] = -1
		} else {
			left[i] = stack[len(stack)-1]
		}
		stack = append(stack, i)
	}

	stack = []int{}
	for i := len(heights) - 1; i >= 0; i-- {
		for len(stack) > 0 && heights[stack[len(stack)-1]] >= heights[i] {
			stack = stack[:len(stack)-1]
		}
		if len(stack) == 0 {
			right[i] = len(heights)
		} else {
			right[i] = stack[len(stack)-1]
		}
		stack = append(stack, i)
	}
	ans := heights[0]
	for i := 0; i < len(heights); i++ {
		ans = max(ans, (right[i]-left[i]-1)*heights[i])
	}
	return ans
}
