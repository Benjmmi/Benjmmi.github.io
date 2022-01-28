package sample

import "sort"

type ints [][]int

func (s ints) Len() int           { return len(s) }
func (s ints) Less(i, j int) bool { return s[i][0] < s[j][0] }
func (s ints) Swap(i, j int)      { s[i], s[j] = s[j], s[i] }

func minMeetingRooms(intervals [][]int) int {
	// endTime
	minHeap := []int{}
	sort.Sort(ints(intervals))
	minHeap = append(minHeap, intervals[0][1])

	intervals = intervals[1:]
	for _, interval := range intervals {
		startTime := interval[0]
		endTime := interval[1]

		k := 0
		for {
			if !(k < len(minHeap)) {
				minHeap = append(minHeap, endTime)
				break
			}
			if minHeap[k] <= startTime {
				front := append(minHeap[:k], endTime)
				last := minHeap[k+1:]
				minHeap = append(front, last...)
				break
			}
			k++

		}
	}

	return len(minHeap)
}

//func minMeetingRooms(intervals [][]int) int {
//	if len(intervals) == 0 {
//		return 0
//	}
//	if len(intervals) == 1 {
//		return 1
//	}
//	startTime := []int{}
//	endTime := []int{}
//	for i := 0; i < len(intervals); i++ {
//		startTime = append(startTime, intervals[i][0])
//		endTime = append(endTime, intervals[i][1])
//	}
//
//	sort.Ints(startTime)
//	sort.Ints(endTime)
//	rooms := 1
//	endPtr := 0
//	startPtr := 0
//	for ; startPtr < len(startTime); startPtr++ {
//		if startTime[startPtr] >= endTime[endPtr] {
//			room := startPtr - endPtr
//			if rooms < room {
//				rooms = room
//			}
//			endPtr++
//		}
//	}
//
//	room := startPtr - endPtr
//	if rooms < room {
//		rooms = room
//	}
//
//	return rooms
//}
