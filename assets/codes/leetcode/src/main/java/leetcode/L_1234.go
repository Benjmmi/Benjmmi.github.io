package sample

import (
	"math"
	"strings"
)

func balancedString(s string) int {
	Q := strings.Count(s, "Q")
	W := strings.Count(s, "W")
	E := strings.Count(s, "E")
	R := strings.Count(s, "R")

	length := len(s) / 4
	if Q > length {
		Q = int(math.Abs(float64(length - Q)))
	} else {
		Q = 0
	}
	if W > length {
		W = int(math.Abs(float64(length - W)))
	} else {
		W = 0
	}
	if E > length {
		E = int(math.Abs(float64(length - E)))
	} else {
		E = 0
	}
	if R > length {
		R = int(math.Abs(float64(length - R)))
	} else {
		R = 0
	}
	return Q + W + E + R
}
