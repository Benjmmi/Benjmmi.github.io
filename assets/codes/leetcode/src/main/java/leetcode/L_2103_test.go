package sample

import (
	"fmt"
	"testing"
)

func Test_countPoints(t *testing.T) {
	fmt.Println(countPoints("B0B6G0R6R0R6G9"))
	fmt.Println(countPoints("B0R0G0R9R0B0G0"))
	fmt.Println(countPoints("G4"))
}
