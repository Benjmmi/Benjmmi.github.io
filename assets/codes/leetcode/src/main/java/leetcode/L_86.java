package leetcode;

public class L_86 {
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

    public ListNode partition(ListNode head, int x)
    {
        if (head == null){
            return head;
        }
        ListNode left = new ListNode();
        ListNode t_l = left;
        ListNode right = new ListNode();
        ListNode t_r = right;

        while (head != null){
            if (head.val >= x){
                right.next = new ListNode(head.val);
                right = right.next;
            } else {
                left.next = new ListNode(head.val);
                left = left.next;
            }
            head = head.next;
        }

        left.next = t_r.next;
        return t_l.next;
    }

    public static void main(String[] args) {
        L_86 l86 = new L_86();
        ListNode listNode;
        {
            listNode = new ListNode(1);
            listNode.next = new ListNode(4);
            listNode.next.next = new ListNode(3);
            listNode.next.next.next = new ListNode(2);
            listNode.next.next.next.next = new ListNode(5);
            listNode.next.next.next.next.next = new ListNode(2);
            println(l86.partition(listNode, 2));
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
