package sample

func mergeTwoLists(list1 *ListNode, list2 *ListNode) *ListNode {
	c := list1
	for list1 != nil && list2 != nil {
		if list1.Val <= list2.Val {
			b := list1.Next
			list1.Next = list2
			list2 = list2.Next
			list1 = list1.Next
			list1.Next = b
		} else {
			b := list1.Next
			list1.Next = list2
			list2 = list2.Next
			list1 = list1.Next
			list1.Next = b
			list1.Val, list1.Next.Val = list1.Next.Val, list1.Val
		}
	}

	if list2 != nil {
		list1.Next = list2
	}
	return c
}
