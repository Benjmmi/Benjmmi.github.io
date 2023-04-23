package sample

func longestArithSeqLength(nums []int) int {
	n, ans := len(nums), 0
	dp := make([][]int, n)
	for i := 0; i < n; i++ {
		dp[i] = make([]int, 1010)
	}
	for i := 0; i < n; i++ {
		for j := 0; j < i; j++ {
			d := nums[i] - nums[j] + 500
			dp[i][d] = max(dp[i][d], dp[j][d]+1)
			ans = max(ans, dp[i][d])
		}
	}
	return ans + 1
}

//func longestArithSeqLength(nums []int) int {
//	set := map[int][]int{}
//	visited := map[string]int{}
//
//	for i := 0; i < len(nums); i++ {
//		if set[nums[i]] != nil {
//			set[nums[i]] = append(set[nums[i]], i)
//		} else {
//			set[nums[i]] = []int{i}
//		}
//	}
//
//	maxLen := 0
//	for i := 0; i < len(nums) && len(nums)-i > maxLen; i++ {
//		for k := i + 1; k < len(nums) && len(nums)-i > maxLen; k++ {
//			rootNum := 2
//			last := nums[k]
//			lastIndex := k
//			subVal := nums[i] - last
//			key := strings.Join([]string{strconv.Itoa(lastIndex), strconv.Itoa(subVal)}, "_")
//			if _, exist := visited[key]; exist {
//				continue
//			}
//			for {
//				mark := false
//				last -= subVal
//				if v, e := set[last]; e {
//					for i := 0; i < len(v); i++ {
//						if v[i] > lastIndex {
//							lastIndex = v[i]
//							mark = true
//							break
//						}
//					}
//					if mark {
//						rootNum++
//						key := strings.Join([]string{strconv.Itoa(lastIndex), strconv.Itoa(subVal)}, "_")
//						if total, exist := visited[key]; exist {
//							if rootNum >= total {
//								visited[key] = rootNum
//							} else {
//								break
//							}
//						} else {
//							visited[key] = rootNum
//						}
//					} else {
//						break
//					}
//				} else {
//					break
//				}
//			}
//			if rootNum > maxLen {
//				maxLen = rootNum
//			}
//		}
//	}
//	return maxLen
//}
