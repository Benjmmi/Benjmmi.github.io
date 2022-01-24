package sample

import "container/list"

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
	buss2 := map[int]bool{}

	stops := list.New()
	stops.PushBack(source)

	for stops.Len() > 0 {
		st := stops.Front().Value.(int)
		stops.Remove(stops.Front())
		if st == target {
			return len(buss2)
		}
		for bus, _ := range stopToBus[st] {
			if _, e := buss[bus]; !e {
				buss[bus] = true
				for s, _ := range busToStop[bus] {
					stops.PushBack(s)
				}
			}
		}
	}
	return -1
}
