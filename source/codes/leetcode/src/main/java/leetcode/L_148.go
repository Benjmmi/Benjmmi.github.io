package sample

func sortList(head *ListNode) *ListNode {

	if head == nil || head.Next == nil {
		return head
	}
	fast, slow := head.Next, head
	for fast != nil && fast.Next != nil {
		slow = slow.Next
		fast = fast.Next.Next
	}
	tmp := slow.Next
	slow.Next = nil
	left := sortList(head)
	right := sortList(tmp)
	h := &ListNode{}
	res := h
	for left != nil && right != nil {
		if left.Val > right.Val {
			h.Next = left
			left = left.Next
		} else {
			h.Next = right
			right = right.Next
		}
		h = h.Next
	}

	if left == nil {
		h.Next = right
		return res.Next
	} else {
		h.Next = left
		return res.Next
	}
}
