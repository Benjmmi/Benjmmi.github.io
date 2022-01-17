package sample

func twoSum(nums []int, target int) []int {
	if len(nums) < 2 {
		return nil
	}
	numsMap := map[int][]int{}

	for i, num := range nums {
		if _, exist := numsMap[num]; exist {
			numsMap[num] = append(numsMap[num], i)
		} else {
			numsMap[num] = []int{i}
		}
	}

	if target%2 == 0 {
		t := target / 2
		n, exist := numsMap[t]
		if exist && len(n) > 1 {
			return []int{n[0], n[1]}
		}
		delete(numsMap, t)
	}

	for i, n := range numsMap {
		if v, exist := numsMap[target-i]; exist {
			return []int{n[0], v[0]}
		}
	}
	return nil
}
