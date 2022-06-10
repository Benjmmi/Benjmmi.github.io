package sample

//func largestRectangleArea(heights []int) int {
//	type vi struct {
//		val   int
//		index int
//	}
//	var stack []vi
//	ans := heights[0]
//	for i := 0; i < len(heights); i++ {
//		if len(stack) == 0 {
//			stack = append(stack, vi{val: heights[i], index: i})
//			continue
//		}
//		if !(stack[len(stack)-1].val <= heights[i]) {
//			for len(stack) > 0 && stack[len(stack)-1].val > heights[i] {
//				last := stack[len(stack)-1]
//				ans = max((i-last.index)*last.val, ans)
//				ans = max((i-last.index+1)*heights[i], ans)
//				stack = stack[:len(stack)-1]
//			}
//		}
//		stack = append(stack, vi{val: heights[i], index: i})
//	}
//	for len(stack) > 1 {
//		last := stack[len(stack)-1]
//		ans = max((len(heights)-stack[len(stack)-2].index-1)*last.val, ans)
//		stack = stack[:len(stack)-1]
//	}
//	// 最后一个一定时最小的
//	ans = max(stack[0].val*len(heights), ans)
//
//	return ans
//}

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
