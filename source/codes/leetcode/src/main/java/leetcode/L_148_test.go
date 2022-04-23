package sample

import (
	"fmt"
	"testing"
)

func Test_sortList(t *testing.T) {
	a := &ListNode{
		Val: 4,
		Next: &ListNode{
			Val: 2,
			Next: &ListNode{
				Val: 1,
				Next: &ListNode{
					Val: 3,
				},
			},
		},
	}
	b := sortList(a)
	for b != nil {
		fmt.Println(b.Val)
		b = b.Next
	}
}
