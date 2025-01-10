package sample

/*
*
投票啊 ，投票 反正我能赢我怕啥
*/
func majorityElement(nums []int) int {
	votes := 0
	value := 0
	for i := 0; i < len(nums); i++ {
		if votes == 0 {
			value = nums[i]
		}
		if value == nums[i] {
			votes++
		} else {
			votes--
		}
	}
	return value
}

//
//func majorityElement(nums []int) int {
//	sort.Ints(nums)
//	value := nums[0]
//
//	maxDuplicate := 0
//	duplicate := 0
//
//	currentNum := nums[0]
//
//	for _, num := range nums {
//		if num == currentNum {
//			duplicate++
//		}
//		if duplicate > maxDuplicate {
//			maxDuplicate = duplicate
//			value = num
//		}
//		if num != currentNum {
//			duplicate = 1
//			currentNum = num
//		}
//	}
//	return value
//}
