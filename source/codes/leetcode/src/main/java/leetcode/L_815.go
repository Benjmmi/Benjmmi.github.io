package sample

func numBusesToDestination(routes [][]int, source int, target int) int {

	n := len(routes)

	stopToBus := map[int]map[int]bool{}
	busToStop := map[int]map[int]bool{}

	for i := 0; i < n; i++ {
		for j := 0; j < len(routes[i]); j++ {
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

	buss := map[int]bool{}
	bussDeepth := map[int]int{}

	stops := []int{source}
	//stops.PushBack(source)
	bussDeepth[source] = 0
	for len(stops) > 0 {
		st := stops[0]
		stops = stops[1:]
		//stops.Remove(stops.Front())
		if st == target {
			return bussDeepth[st]
		}
		depth := bussDeepth[st] + 1
		for bus, _ := range stopToBus[st] {
			if _, e := buss[bus]; !e {
				buss[bus] = true
				for s, _ := range busToStop[bus] {
					stops = append(stops, s)
					if _, v := bussDeepth[s]; !v {
						if bussDeepth[s] < depth {
							bussDeepth[s] = depth
						}
					} else {
						bussDeepth[s] = depth
					}
				}
			}
		}
	}
	return -1
}
