package leetcode;

public class L_92 {
    public static class ListNode {
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

    public ListNode reverseBetween(ListNode head, int left, int right) {

        ListNode dummyNode = new ListNode(-1);
        dummyNode.next = head;
        ListNode pre = dummyNode;
        for (int i = 0; i < left - 1; i++) {
            pre = pre.next;
        }

        ListNode cur = pre.next;
        ListNode next;
        for (int i = 0; i < right - left; i++) {
            next = cur.next;
            cur.next = next.next;
            next.next = pre.next;
            pre.next = next;
        }
        return dummyNode.next;

//        if (left == right) {
//            return head;
//        }
//        if (head == null) {
//            return head;
//        }
//        ListNode curr = head;
//        ListNode preM = null;
//        int i = 0;
//        while (i < left - 1) {
//            preM = curr;
//            curr = curr.next;
//            i++;
//        }
//
//
//        ListNode pre = null;
//        ListNode lastM = null;
//        while (i < right) {
//            i++;
//            ListNode temp = null;
//            if (i < right) {
//                temp = curr.next;
//            } else {
//                lastM = curr.next;
//            }
//            curr.next = pre;
//            pre = curr;
//            curr = temp;
//        }
//        ListNode preB = pre;
//        while (true) {
//            if (preB.next == null) {
//                preB.next = lastM;
//                break;
//            }
//            preB = preB.next;
//        }
//
//        if (preM == null) {
//            return pre;
//        } else {
//            preM.next = pre;
//        }


//        return head;
    }

    public static void main(String[] args) {
        L_92 l92 = new L_92();
        ListNode listNode;
        {
            listNode = new ListNode(1);
            listNode.next = new ListNode(2);
            listNode.next.next = new ListNode(3);
            listNode.next.next.next = new ListNode(4);
            listNode.next.next.next.next = new ListNode(5);
            println(l92.reverseBetween(listNode, 1, 4));
        }
        {
            listNode = new ListNode(1);
            listNode.next = new ListNode(2);
            listNode.next.next = new ListNode(3);
            listNode.next.next.next = new ListNode(4);
            listNode.next.next.next.next = new ListNode(5);
            println(l92.reverseBetween(listNode, 2, 4));
        }
        {
            listNode = new ListNode(1);
            listNode.next = new ListNode(2);
            listNode.next.next = new ListNode(3);
            listNode.next.next.next = new ListNode(4);
            listNode.next.next.next.next = new ListNode(5);
            println(l92.reverseBetween(listNode, 2, 5));
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
