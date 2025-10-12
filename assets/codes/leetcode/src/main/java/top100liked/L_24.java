package top100liked;

public class L_24 {
    // TODO 多练习
    public ListNode swapPairs(ListNode head) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode node0 = dummy;
        ListNode node1 = head;

        while (node1 != null && node1.next != null) {
            ListNode node2 = node1.next;
            ListNode node3 = node2.next;

            node0.next = node2;
            node2.next = node1;
            node1.next = node3;

            node0 = node1;
            node1 = node3;
        }

        return dummy.next;
    }

    public static void main(String[] args) {
        L_24 l24 = new L_24();
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        System.out.println(l24.swapPairs(head));
    }
}
