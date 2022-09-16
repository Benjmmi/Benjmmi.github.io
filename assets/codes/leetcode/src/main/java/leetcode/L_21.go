package sample

func mergeTwoLists(list1 *ListNode, list2 *ListNode) *ListNode {
	c := list1
	for list1.Next != nil && list2.Next != nil {
		if list1.Val <= list2.Val {
			b := list1.Next
			list1.Next = list2
			list2 = list2.Next
			list1.Next.Next = b
		} else {
			list1.Val, list2.Val = list2.Val, list1.Val
		}
	}
	return c
}
