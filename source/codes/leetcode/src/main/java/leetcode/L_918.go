package sample

import "container/list"

func maxSubarraySumCircular(nums []int) int {

	if len(nums) < 1 {
		return 0
	}
	if len(nums) == 1 {
		return nums[0]
	}
	bak := make([]int, len(nums))
	copy(bak, nums)
	findMaxSubarraySumCircular(nums)
	max := nums[0]
	maxNum := nums[0]
	sum := 0
	for i := 0; i < len(nums); i++ {
		if nums[i] > max {
			max = nums[i]
		}
		sum += bak[i]
		if bak[i] > maxNum {
			maxNum = bak[i]
		}
		bak[i] = -bak[i]
	}

	if maxNum < 0 {
		return maxNum
	}

	findMaxSubarraySumCircular(bak)

	for i := 0; i < len(bak); i++ {
		if bak[i]+sum > max {
			max = bak[i] + sum
		}
	}

	return max
}

func preSum(num []int) int {
	N := len(num)
	P := make([]int, 2*N+1)
	for i := 0; i < 2*N; i++ {
		P[i+1] = P[i] + num[i%N]
	}
	ans := num[0]

	deque := list.New()
	deque.PushFront(0)

	for i := 1; i < 2*N; i++ {
		if deque.Front().Value.(int) < i-N {
			deque.Remove(deque.Front())
		}
		f := deque.Front().Value.(int)
		ans = max(ans, P[i]-P[f])
		l := deque.Back().Value.(int)
		for !(deque.Len() == 0) && P[i] <= P[l] {
			deque.Remove(deque.Back())
		}
		deque.PushBack(i)
	}

	return ans
}

func findMaxSubarraySumCircular(nums []int) {

	for i := 1; i < len(nums); i++ {
		if nums[i]+nums[i-1] > nums[i] {
			nums[i] = nums[i] + nums[i-1]
		}
	}
}

func compressArray(nums []int) (compress []int) {
	compress = make([]int, len(nums))
	j := 0
	b := false
	for i := 0; i < len(nums); i++ {
		if nums[i] <= 0 {
			compress[j] = nums[i]
			for k := i + 1; k < len(nums); k++ {
				if nums[k] <= 0 {
					compress[j] += nums[k]
					i++
				} else {
					break
				}
			}
		}
		if nums[i] > 0 {
			if !b {
				b = true
			}
			compress[j] = nums[i]
			for k := i + 1; k < len(nums); k++ {
				if nums[k] > 0 {
					compress[j] += nums[k]
					i++
				} else {
					break
				}
			}
		}
		j++
	}
	if b {
		compress = compress[:j]
	} else {
		compress = nums
	}
	return
}
