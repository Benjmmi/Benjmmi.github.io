package sample

import (
	"fmt"
	"sort"
	"strconv"
	"strings"
)

func findMissingRanges(nums []int, lower int, upper int) []string {

	if lower == upper && len(nums) == 0 {
		return []string{strconv.Itoa(upper)}
	}

	if len(nums) == 0 {
		return []string{fmt.Sprintf("%d->%d", lower, upper)}
	}
	s := []string{}
	if nums[0] != lower {
		s = append(s, strconv.Itoa(lower))
	}
	nums = append(nums, lower)
	nums = append(nums, upper)
	sort.Ints(nums)
	for i := 1; i < len(nums); i++ {
		if nums[i]-nums[i-1] == 2 {
			s = append(s, strconv.Itoa(nums[i-1]+1))
		} else if nums[i]-nums[i-1] > 2 {
			if nums[i] == upper {
				s = append(s, fmt.Sprintf("%d->%d", nums[i-1]+1, upper))
			} else {
				s = append(s, fmt.Sprintf("%d->%d", nums[i-1]+1, nums[i]-1))
			}
		}
	}

	if len(s) != 0 && !strings.Contains(s[len(s)-1], strconv.Itoa(upper)) {
		s = append(s, strconv.Itoa(upper))
	}

	return s
}
