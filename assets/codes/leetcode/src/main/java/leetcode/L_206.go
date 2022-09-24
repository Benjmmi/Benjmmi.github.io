package sample

func reverseList(head *ListNode) *ListNode {
	if head == nil || head.Next == nil {
		return head
	}
	r := reverseList(head.Next)
	head.Next.Next = head
	head.Next = nil
	return r
}
