package top100liked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class L_234 {
    public boolean isPalindrome(ListNode head) {
//        List<Integer> list = new ArrayList<>();
//        while (head != null) {
//            list.add(head.val);
//            head = head.next;
//        }
//        String a = list.toString();
//        Collections.reverse(list);
//        String b = list.toString();
//        return a.equals(b);

        int count = 0;
        ListNode dummy = head;
        while (dummy != null) {
            count++;
            dummy = dummy.next;
        }
        dummy = head;
        count /= 2;
        while (count > 0) {
            dummy = dummy.next;
            count--;
        }
        ListNode pre = null;
        ListNode cur = dummy;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        while (pre != null) {
            if (pre.val != head.val) return false;
            pre = pre.next;
            head = head.next;
        }


        return true;
    }

    public static void main(String[] args) {
        L_234 l234 = new L_234();
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(2);
        head.next.next.next.next = new ListNode(1);
        System.out.println(l234.isPalindrome(head));

    }
}
