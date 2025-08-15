package leetcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 给你两个 非空 的链表，表示两个非负的整数。它们每位数字都是按照 逆序 的方式存储的，并且每个节点只能存储 一位 数字。
 *
 * 请你将两个数相加，并以相同形式返回一个表示和的链表。
 *
 * 你可以假设除了数字 0 之外，这两个数都不会以 0 开头。
 *
 *
 *
 * 示例 1：
 *
 *
 * 输入：l1 = [2,4,3], l2 = [5,6,4]
 * 输出：[7,0,8]
 * 解释：342 + 465 = 807.
 * 示例 2：
 *
 * 输入：l1 = [0], l2 = [0]
 * 输出：[0]
 * 示例 3：
 *
 * 输入：l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9]
 * 输出：[8,9,9,9,0,0,0,1]
 *
 *
 * 提示：
 *
 * 每个链表中的节点数在范围 [1, 100] 内
 * 0 <= Node.val <= 9
 * 题目数据保证列表表示的数字不含前导零
 */
public class L_2 {
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

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode result = null;
        ListNode temp = null;
        int cal = 0;
        while (l1 != null || l2 != null) {
            int l1_value = 0;
            int l2_value = 0;
            if (l1 != null) {
                l1_value = l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                l2_value = l2.val;
                l2 = l2.next;
            }
            int value = l1_value + l2_value + cal;
            if (temp == null) {
                temp = new ListNode(value % 10);
                result = temp;
            } else {
                temp.next = new ListNode(value % 10);
                temp = temp.next;
            }
            cal = value / 10;
        }
        if (cal >0){
            temp.next = new ListNode(1);
        }

        return result;
    }

//    public Integer addTwoNumbers(ListNode result, ListNode l1, ListNode l2) {
//        if (l1 == null && l2 == null) {
//            return null;
//        }
//
//        result.next = new ListNode();
//        int l1_value = l1 != null ? l1.val : 0;
//        int l2_value = l2 != null ? l2.val : 0;
//
//
//        Integer r = addTwoNumbers(result.next, l1 == null ? null : l1.next, l2 == null ? null : l2.next);
//        if (r == null) {
//            result.next = null;
//        }
//        int value = l1_value + l2_value + (r == null ? 0 : r);
//        result.val = value % 10;
//        return value / 10;
//    }

    public static void main(String[] args) {
        L_2 l22 = new L_2();
        {
            ListNode l1 = new ListNode();
            l1.val = 9;
            l1.next = new ListNode(9);
            l1.next.next = new ListNode(9);
            l1.next.next.next = new ListNode(9);
            l1.next.next.next.next = new ListNode(9);
            l1.next.next.next.next.next = new ListNode(9);

            ListNode l2 = new ListNode();
            l2.val = 9;
            l2.next = new ListNode(9);
            l2.next.next = new ListNode(9);
            l2.next.next.next = new ListNode(9);
            println(l22.addTwoNumbers(l1, l2));
        }
        {
            ListNode l1 = new ListNode();
            l1.val = 2;
            l1.next = new ListNode(4);
            l1.next.next = new ListNode(3);

            ListNode l2 = new ListNode();
            l2.val = 5;
            l2.next = new ListNode(6);
            l2.next.next = new ListNode(4);
            println(l22.addTwoNumbers(l1, l2));
        }
        {
            ListNode l1 = new ListNode();
            l1.val = 0;

            ListNode l2 = new ListNode();
            l2.val = 0;
            println(l22.addTwoNumbers(l1, l2));
        }
    }

    static void println(ListNode result) {
        while (result != null) {
            System.out.print(result.val);
            result = result.next;
        }
        System.out.println();
    }
}
