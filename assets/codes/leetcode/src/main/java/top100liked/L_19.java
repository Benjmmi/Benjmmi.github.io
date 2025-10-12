package top100liked;

public class L_19 {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode delete = dummy;
        int i = 0;
        while (head != null) {
            if (i >= n) {
                delete = delete.next;
            }
            head = head.next;
            i++;
        }
        if (delete != null && delete.next != null) {
            delete.next = delete.next.next;
        }

        return dummy.next;
    }

    public static void main(String[] args) {
        L_19 l19 = new L_19();
        ListNode head = new ListNode(1);
        System.out.println(l19.removeNthFromEnd(head, 1));
    }
}
