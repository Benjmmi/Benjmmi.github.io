package leetcode;

public class L_82 {

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


    public ListNode deleteDuplicates(ListNode head) {
        ListNode dumpNode = new ListNode();
        dumpNode.next = head;
        ListNode temp = dumpNode;
        while (temp != null) {
            if (temp.next.val == temp.next.next.val) {
                int value = temp.next.val;
            }
        }

        return dumpNode.next;
    }


    public static void main(String[] args) {
        L_82 l82 = new L_82();
        ListNode listNode;
        {
            listNode = new ListNode(1);
            listNode.next = new ListNode(2);
            listNode.next.next = new ListNode(2);
            listNode.next.next.next = new ListNode(4);
            listNode.next.next.next.next = new ListNode(5);
            listNode.next.next.next.next.next = new ListNode(6);
            println(l82.deleteDuplicates(listNode));
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
