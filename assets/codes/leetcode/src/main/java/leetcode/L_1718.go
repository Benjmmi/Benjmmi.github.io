package sample

import (
	"fmt"
	"strings"
)

func constructDistancedSequence(n int) []int {
	if n == 1 {
		return []int{1}
	}
	result := make([]int, n*n)
	for i := n; i >= 0; i-- {
		result[0] = i
		result[i] = i
		if solve(result, 1, n, 0, fmt.Sprintf("@%d@", i)) {
			for i := len(result) - 1; i >= 0; i-- {
				if result[i] != 0 {
					result = result[:i+1]
					break
				}
			}
			return result
		}
	}
	return nil
}

func solve(result []int, cur, length, index int, curStr string) bool {
	if cur == length {
		border := -1
		for i := len(result) - 1; i >= 0; i-- {
			if result[i] != 0 {
				border = i
				break
			}
		}
		for i := border; i >= 0; i-- {
			if result[i] == 0 {
				return false
			}
		}
		return true
	}
	for i := 0; i < len(result); i++ {
		if result[i] == 0 {
			index = i
			break
		}
	}
	for i := length; i > 0; i-- {
		if i == 1 && result[index] == 0 {
			goto jump
		}
		if result[index] != 0 || result[index+i] != 0 {
			continue
		}
	jump:
		if strings.Contains(curStr, fmt.Sprintf("@%d@", i)) {
			continue
		}
		if i != 1 {
			result[index] = i
			result[index+i] = i
			if solve(result, cur+1, length, index, fmt.Sprintf("%s@%d@", curStr, i)) {
				return true
			} else {
				result[index] = 0
				result[index+i] = 0
			}
		} else {
			result[index] = i
			if solve(result, cur+1, length, index, fmt.Sprintf("%s@%d@", curStr, i)) {
				return true
			} else {
				result[index] = 0
			}
		}
	}

	return false
}
