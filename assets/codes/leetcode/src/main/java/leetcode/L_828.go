package sample

func uniqueLetterString(s string) int {
	if len(s) <= 1 {
		return len(s)
	}
	total := 0
	preChartIndex := make([]int, 26)
	curIndex := make([]int, 26)

	for i := range preChartIndex {
		preChartIndex[i] = -1
		curIndex[i] = -1
	}

	for i, c := range s {
		index := c - 'A'
		if curIndex[index] > -1 {
			total += (i - curIndex[index]) * (curIndex[index] - preChartIndex[index])
		}
		preChartIndex[index] = curIndex[index]
		curIndex[index] = i
	}
	for i := 0; i < 26; i++ {
		if curIndex[i] > -1 {
			total += (curIndex[i] - preChartIndex[i]) * (len(s) - curIndex[i])
		}
	}

	return total
}
