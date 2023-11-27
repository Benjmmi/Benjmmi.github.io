package sample

import (
	"fmt"
	"testing"
)

func Test_mergeTwoLists(t *testing.T) {
	list1, list2 := &ListNode{Val: 1, Next: &ListNode{2, &ListNode{Val: 4}}}, &ListNode{Val: 1, Next: &ListNode{3, &ListNode{Val: 4}}}
	c := mergeTwoLists(list2, list1)
	for c != nil {
		fmt.Println(c.Val)
		c = c.Next
	}
}
