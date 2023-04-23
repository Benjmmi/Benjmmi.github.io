package sample

import (
	"fmt"
	"testing"
)

func Test_mergeTwoLists(t *testing.T) {
	list1, list2 := &ListNode{}, &ListNode{}
	list1.Val = 1
	list1.Next = &ListNode{2, &ListNode{Val: 4}}
	list2.Val = 1
	list2.Next = &ListNode{3, &ListNode{Val: 4}}
	c := mergeTwoLists(list2, list1)
	for c != nil {
		fmt.Println(c.Val)
		c = c.Next
	}
}
