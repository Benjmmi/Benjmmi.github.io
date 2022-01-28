package sample

import (
	"sort"
)

func minMeetingRooms(intervals [][]int) int {
	if len(intervals) == 0 {
		return 0
	}
	if len(intervals) == 1 {
		return 1
	}
	startTime := []int{}
	endTime := []int{}
	for i := 0; i < len(intervals); i++ {
		startTime = append(startTime, intervals[i][0])
		endTime = append(endTime, intervals[i][1])
	}

	sort.Ints(startTime)
	sort.Ints(endTime)
	rooms := 1
	endPtr := 0
	startPtr := 0
	for ; startPtr < len(startTime); startPtr++ {
		if startTime[startPtr] >= endTime[endPtr] {
			room := startPtr - endPtr
			if rooms < room {
				rooms = room
			}
			endPtr++
		}
	}

	room := startPtr - endPtr
	if rooms < room {
		rooms = room
	}

	return rooms
}
