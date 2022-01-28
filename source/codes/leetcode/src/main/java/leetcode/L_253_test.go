package sample

import (
	"fmt"
	"testing"
)

func Test_minMeetingRooms(t *testing.T) {
	fmt.Println(minMeetingRooms([][]int{{0, 30}, {5, 10}, {15, 20}}))
	fmt.Println(minMeetingRooms([][]int{{7, 10}, {2, 4}}))
	fmt.Println(minMeetingRooms([][]int{{5, 8}, {6, 8}}))
	fmt.Println(minMeetingRooms([][]int{{2, 7}}))
}
