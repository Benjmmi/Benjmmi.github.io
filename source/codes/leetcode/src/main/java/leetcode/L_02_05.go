package sample

/**
 * Definition for singly-linked list.
 *
 *
 *
 * }
 */

type ListNode struct {
	Val  int
	Next *ListNode
}

func addTwoNumbers(l1 *ListNode, l2 *ListNode) *ListNode {
	result := &ListNode{}

	s := result
	ea := 0
	for {
		var v1 int
		var v2 int
		if l1 != nil {
			v1 = l1.Val
			l1 = l1.Next
		}
		if l2 != nil {
			v2 = l2.Val
			l2 = l2.Next
		}

		v := v1 + v2 + ea
		vy := v % 10
		ea = v / 10
		result.Val = vy
		if !(l1 == nil && l2 == nil) {
			result.Next = &ListNode{}
			result = result.Next
		} else {
			break
		}
	}

	if ea != 0 {
		result.Next = &ListNode{Val: ea}
	}

	return s
}
