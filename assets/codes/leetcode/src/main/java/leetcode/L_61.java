package leetcode;

public class L_61 {
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

    public ListNode rotateRight(ListNode head, int k) {
        if (k == 0 || head == null) {
            return head;
        }
        ListNode dummyNode = new ListNode();
        dummyNode.next = head;
        ListNode temp = head;
        int cnt = 0;
        while (temp != null) {
            temp = temp.next;
            cnt++;
        }
        k %= cnt;
        if (k == 0){
            return head;
        }
        ListNode slow = dummyNode;
        ListNode fast = dummyNode;
        int i = 0;
        while (fast.next != null) {
            if (i >= k) {
                slow = slow.next;
            }
            fast = fast.next;
            i++;
        }
        ListNode newH = slow.next;
        fast.next = dummyNode.next;
        slow.next = null;



        return newH;
    }

    public static void main(String[] args) {
        L_61 l61 = new L_61();
        ListNode listNode;
        {
            listNode = new ListNode(1);
            listNode.next = new ListNode(2);
            println(l61.rotateRight(listNode, 1));
        }
        {
            listNode = new ListNode(1);
            listNode.next = new ListNode(2);
            listNode.next.next = new ListNode(3);
            listNode.next.next.next = new ListNode(4);
            listNode.next.next.next.next = new ListNode(5);
            println(l61.rotateRight(listNode, 2));
        }
        {
            listNode = new ListNode(0);
            listNode.next = new ListNode(1);
            listNode.next.next = new ListNode(2);
            println(l61.rotateRight(listNode, 4));
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
