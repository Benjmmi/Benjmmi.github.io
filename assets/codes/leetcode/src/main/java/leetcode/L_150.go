package sample

/*
*
nums = [1,2,3,4,5,6,7,8,9,10] ,k=3
*/
func rotate(nums []int, k int) {
	length := len(nums)
	if len(nums) <= 1 {
		return
	}

	for i := 0; i < k; i++ {
		value := nums[length-1]
		for j := length - 1; j > 0; j-- {
			nums[j] = nums[j-1]
		}
		nums[0] = value
	}
}
