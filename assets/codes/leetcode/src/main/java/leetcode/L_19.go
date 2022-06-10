package sample

func removeNthFromEnd(head *ListNode, n int) *ListNode {
	var pre, last *ListNode
	pre = head
	last = head
	i := 0
	for last != nil {
		if i > n {
			pre = pre.Next
		}
		last = last.Next
		i++
	}
	if i == 1 {
		return nil
	}
	if n == 1 {
		pre.Next = nil
	}
	pre.Next = pre.Next.Next
	return head
}
