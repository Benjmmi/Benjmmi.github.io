package top100liked;

import java.util.PriorityQueue;

public class L_148 {
    public ListNode sortList(ListNode head) {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        while (head != null) {
            queue.add(head.val);
            head = head.next;
        }
        ListNode dummy = new ListNode(0);
        ListNode temp = dummy;
        while (!queue.isEmpty()) {
            temp.next = new ListNode(queue.poll());
            temp = temp.next;
        }
        return dummy.next;
    }

    public static void main(String[] args) {

    }
}
