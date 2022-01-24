package sample

func numBusesToDestination(routes [][]int, source int, target int) int {

	n := len(routes)
	m := len(routes[0])

	stopToBus := map[int]map[int]bool{}
	busToStop := map[int]map[int]bool{}

	for i := 0; i < n; i++ {
		for j := 0; j < m-1; j++ {
			if stopToBus[routes[i][j]] != nil {
				stopToBus[routes[i][j]][i] = true
			} else {
				stopToBus[routes[i][j]] = map[int]bool{i: true}
			}
			if busToStop[i] != nil {
				busToStop[i][routes[i][j]] = true
			} else {
				busToStop[i] = map[int]bool{routes[i][j]: true}
			}
		}
	}

	exist := map[int]bool{}
	stops := []int{source}
	for _, st := range stops {
		if st == target {
			return 1
		}
		for bus, _ := range stopToBus[st] {
			if _, e := exist[bus]; !e {
				for s, _ := range busToStop[bus] {
					stops = append(stops, s)
				}
			}
		}
	}

	return -1
}
