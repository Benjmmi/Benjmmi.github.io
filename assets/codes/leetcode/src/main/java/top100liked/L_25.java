package top100liked;

public class L_25 {
    public ListNode reverseKGroup(ListNode head, int k) {
        if (k == 1) {
            return head;
        }
        ListNode end = head;
        int i = k;
        while (i > 0 && end != null) {
            end = end.next;
            i--;
        }
        if (i > 0) {
            return head;
        }

        ListNode pre = null;
        ListNode cur = head;
        while (cur != end) {
            ListNode next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        head.next = reverseKGroup(end, k);
        return pre;
    }


    public static void main(String[] args) {
        L_25 l25 = new L_25();
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        l25.reverseKGroup(head, 2);
    }
}
