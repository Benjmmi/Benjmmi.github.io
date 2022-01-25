package sample

func numBusesToDestination(routes [][]int, source int, target int) int {
	if source == target {
		return 0
	}
	n := len(routes)

	stopToLine := map[int]map[int]bool{}
	lineToStop := map[int]map[int]bool{}

	for i := 0; i < n; i++ {
		for j := 0; j < len(routes[i]); j++ {
			if stopToLine[routes[i][j]] != nil {
				stopToLine[routes[i][j]][i] = true
			} else {
				stopToLine[routes[i][j]] = map[int]bool{i: true}
			}

			if lineToStop[i] != nil {
				lineToStop[i][routes[i][j]] = true
			} else {
				lineToStop[i] = map[int]bool{routes[i][j]: true}
			}
		}
	}

	// 访问过的路线,第几次中转访问
	bused := map[int]int{}
	stoped := map[int]bool{source: true}

	for i, _ := range stopToLine[source] {
		bused[i] = 1
	}

	stops := []int{source}

	for len(stops) > 0 {
		s := stops[0]
		stops = stops[1:]
		for line, _ := range stopToLine[s] {

			for stop, _ := range lineToStop[line] {
				if _, e := stoped[stop]; e {
					continue
				}
				stoped[stop] = true
				if stop == target {
					return bused[line]
				}
				for turn, _ := range stopToLine[stop] {
					if _, e := bused[turn]; !e {
						bused[turn] = bused[line] + 1
						stops = append(stops, stop)
					}
				}
			}
		}
	}

	return -1
}
