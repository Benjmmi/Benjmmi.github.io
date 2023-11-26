package sample

func countPoints(rings string) int {
	if len(rings) < 6 {
		return 0
	}
	length := len(rings)
	rgb := make([]int, len(rings))
	for i := 0; i < length; i += 2 {
		color := rings[i]
		point := rings[i+1]
		p := point - '0'
		if color == 'R' {
			rgb[p] |= 1
		}
		if color == 'G' {
			rgb[p] |= 2
		}
		if color == 'B' {
			rgb[p] |= 4
		}
	}
	count := 0
	for _, ele := range rgb {
		if ele == 7 {
			count++
		}
	}
	return count
}
