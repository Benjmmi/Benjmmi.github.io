package leetcode;

import java.util.ArrayList;
import java.util.List;

public class L_19 {

    static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummyHead = new ListNode();
        dummyHead.next = head;
        ListNode left = dummyHead;
        ListNode right = dummyHead;
        for (int i = 0; i < n; i++) {
            right = right.next;
        }
        while (right.next != null) {
            left = left.next;
            right = right.next;
        }
        left.next = left.next.next;
        return dummyHead.next;
    }

    public static void main(String[] args) {
        L_19 l19 = new L_19();
        ListNode listNode = null;
        {
            listNode = new ListNode(1);
            listNode.next = new ListNode(2);
            listNode.next.next = new ListNode(3);
            listNode.next.next.next = new ListNode(4);
            listNode.next.next.next.next = new ListNode(5);
//            listNode.next.next.next.next.next = new ListNode(6);
            println(l19.removeNthFromEnd(listNode, 2));

        }
    }

    public static void println(ListNode head) {
        while (head != null) {
            System.out.print(head.val + ",");
            head = head.next;
        }
        System.out.println();
    }
}
