package leetcode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class L_148 {
//    public ListNode sortList(ListNode head) {
//        List<Integer> list = new ArrayList<>();
//        while (head != null) {
//            list.add(head.val);
//            head = head.next;
//        }
//        list.sort(new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                return o1 - o2;
//            }
//        });
//        ListNode npy = new ListNode();
//        ListNode tmp = npy;
//        for (Integer integer : list) {
//            tmp.next = new ListNode(integer);
//            tmp = tmp.next;
//        }
//        return npy.next;
//    }


    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode mid = middleNode(head);
        head = sortList(head);
        mid = sortList(mid);

        return mergeListNode(head, mid);
    }

    private ListNode mergeListNode(ListNode head, ListNode mid) {
        ListNode dummyNode = new ListNode();
        ListNode cur = dummyNode;
        while (head != null && mid != null) {
            if (head.val > mid.val) {
                cur.next = mid;
                mid = mid.next;
            } else {
                cur.next = head;
                head = head.next;
            }
            cur = cur.next;
        }
        cur.next = head != null ? head : mid;
        return dummyNode.next;
    }

    public ListNode middleNode(ListNode head) {
        ListNode pre = head;
        ListNode fast = head;
        ListNode slow = head;
        while (fast != null && fast.next != null) {
            pre = slow;
            slow = slow.next;
            fast = fast.next.next;
        }
        pre.next = null;
        return slow;
    }

    public static void main(String[] args) {
        L_148 l148 = new L_148();
        {
            ListNode node = new ListNode(4);
            node.next = new ListNode(2);
            node.next.next = new ListNode(1);
            node.next.next.next = new ListNode(3);
            ListNode l = l148.sortList(node);
            l.println();
        }
    }
}

class ListNode {
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

    public void println() {
        ListNode dummyNode = this;
        while (dummyNode != null) {
            System.out.print(dummyNode.val + ",");
            dummyNode = dummyNode.next;
        }
        System.out.println();
    }
}