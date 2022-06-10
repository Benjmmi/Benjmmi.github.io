package kaoshi

type TreeNode struct {
	Val   int
	Left  *TreeNode
	Right *TreeNode
}

func distributeCandies(candies int, num_people int) []int {
	if num_people == 1 {
		return []int{candies}
	}
	nums := make([]int, num_people)
	i := 1
	j := 0
	for {
		j %= num_people
		nums[j] += i
		j++
		i++
		if candies == 0 {
			break
		}
		if candies-i > 0 {
			candies -= i
		} else {
			i = candies
			candies = 0
		}
	}
	return nums
}

func max(i, j int) int {
	if i > j {
		return i
	}
	return j
}
