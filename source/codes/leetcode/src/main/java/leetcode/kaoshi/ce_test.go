package kaoshi

import (
	"fmt"
	"testing"
)

func Test_Constructor(t *testing.T) {
	obj := Constructor("L1e2t1C1o1d1e1")
	fmt.Printf("%c\n", obj.Next())
	fmt.Printf("%c\n", obj.Next())
	fmt.Printf("%c\n", obj.Next())
	fmt.Printf("%c\n", obj.Next())
	fmt.Printf("%c\n", obj.Next())
	fmt.Printf("%c\n", obj.Next())
	fmt.Printf("%c\n", obj.Next())
	fmt.Printf("%c\n", obj.Next())
	fmt.Printf("%c\n", obj.Next())
	fmt.Printf("%c\n", obj.Next())
	fmt.Printf("%c\n", obj.Next())
	fmt.Println(obj.HasNext())
}
