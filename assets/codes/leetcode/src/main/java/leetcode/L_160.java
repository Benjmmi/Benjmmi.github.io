package leetcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class L_160 {
    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }
    }

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) {
            return null;
        }
        ListNode pA = headA, pB = headB;
        while (pA != pB) {
            pA = (pA == null) ? headB : pA.next;
            pB = (pB == null) ? headA : pB.next;
        }
        return pA;
    }
//
//    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
//        Set<ListNode> mapA = new HashSet<>();
//        while (headA != null){
//            mapA.add(headA);
//            headA = headA.next;
//        }
//        while (headB != null){
//            if (mapA.contains(headB)){
//                return headB;
//            }
//            headB = headB.next;
//        }
//        return null;
//    }

    public static void main(String[] args) {

    }
}
