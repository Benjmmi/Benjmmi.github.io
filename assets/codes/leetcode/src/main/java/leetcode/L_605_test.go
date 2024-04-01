package sample

import (
	"fmt"
	"testing"
)

func Test_canPlaceFlowers(t *testing.T) {
	fmt.Println(canPlaceFlowers([]int{1, 0, 0, 0, 0, 0, 0, 0, 1}, 3) == true)
	fmt.Println(canPlaceFlowers([]int{1, 0, 0, 0, 0, 1}, 2) == false)
	fmt.Println(canPlaceFlowers([]int{1, 0, 0, 0, 1, 0, 0}, 2) == true)
	fmt.Println(canPlaceFlowers([]int{1, 0, 0}, 1) == true)
	fmt.Println(canPlaceFlowers([]int{0}, 1) == true)
	fmt.Println(canPlaceFlowers([]int{0, 0, 1, 0, 0, 0}, 2) == true)
	fmt.Println(canPlaceFlowers([]int{1, 0, 0, 0, 1}, 1) == true)
	fmt.Println(canPlaceFlowers([]int{1, 0, 0, 0, 1, 0, 0, 0, 1}, 1) == true)
	fmt.Println(canPlaceFlowers([]int{1, 0, 0, 0, 1, 0, 0, 0, 1}, 2) == true)
	fmt.Println(canPlaceFlowers([]int{0, 0, 1, 0, 0, 1}, 2) == false)
	fmt.Println(canPlaceFlowers([]int{0, 0, 1, 0, 0, 1, 0, 0, 1}, 2) == false)
	fmt.Println(canPlaceFlowers([]int{0, 0, 1, 0, 0, 1}, 1) == true)
	fmt.Println(canPlaceFlowers([]int{0, 0, 0}, 2) == true)
	fmt.Println(canPlaceFlowers([]int{0, 1, 0}, 0) == true)
	fmt.Println(canPlaceFlowers([]int{0, 1, 1}, 0) == true)
	fmt.Println(canPlaceFlowers([]int{1, 1, 1}, 0) == true)
	fmt.Println(canPlaceFlowers([]int{0, 0, 0}, 2) == true)
	fmt.Println(canPlaceFlowers([]int{0, 1, 0, 0, 0, 1}, 1) == true)
	fmt.Println(canPlaceFlowers([]int{0, 1, 0, 1, 0, 1}, 0) == true)
	fmt.Println(canPlaceFlowers([]int{1}, 0) == true)
	fmt.Println(canPlaceFlowers([]int{1, 0}, 0) == true)
	fmt.Println(canPlaceFlowers([]int{1, 0, 0, 0}, 1) == true)
	fmt.Println(canPlaceFlowers([]int{0, 0, 1, 0}, 1) == true)
	fmt.Println(canPlaceFlowers([]int{0, 0, 1, 1, 1, 1, 0, 0}, 2) == true)
	fmt.Println(canPlaceFlowers([]int{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 6) == true)
	fmt.Println(canPlaceFlowers([]int{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 7) == true)
	fmt.Println(canPlaceFlowers([]int{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, 6) == true)
}
