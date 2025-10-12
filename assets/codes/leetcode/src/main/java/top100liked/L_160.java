package top100liked;

import java.util.HashSet;
import java.util.Set;

public class L_160 {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        Set<ListNode> listNodeSet = new HashSet<>();
        while (headA != null) {
            listNodeSet.add(headA);
            headA = headA.next;
        }
        while (headB != null && !listNodeSet.contains(headB)) {
            headB = headB.next;

        }
        return headB;
    }

    public static void main(String[] args) {

    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
        next = null;
    }
}