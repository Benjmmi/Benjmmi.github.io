package sample

import (
	"fmt"
	"testing"
)

func Test_addTwoNumbers(t *testing.T) {
	// 617
	l1 := &ListNode{Val: 7, Next: &ListNode{Val: 1, Next: &ListNode{Val: 6}}}
	// 295
	l2 := &ListNode{Val: 5, Next: &ListNode{Val: 9, Next: &ListNode{Val: 2}}}

	a := addTwoNumbers(l1, l2)
	for a != nil {
		fmt.Println(a.Val)
		a = a.Next
	}
}

func Test_addTwoNumbers2(t *testing.T) {
	// 617
	l1 := &ListNode{Val: 5}
	// 295
	l2 := &ListNode{Val: 5}

	a := addTwoNumbers(l1, l2)
	for a != nil {
		fmt.Println(a.Val)
		a = a.Next
	}
}
