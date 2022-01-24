package sample

import "testing"

func Test_numBusesToDestination(t *testing.T) {

	numBusesToDestination([][]int{{1, 2, 7}, {3, 6, 7}}, 1, 6)
}
