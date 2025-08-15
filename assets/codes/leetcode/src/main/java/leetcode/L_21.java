package leetcode;

public class L_21 {

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

    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode result = null;
        ListNode temp = null;
        while (list1 != null || list2 != null) {
            Integer list1Value = null;
            Integer list2Value = null;
            if (list1 != null) {
                list1Value = list1.val;
            }
            if (list2 != null) {
                list2Value = list2.val;
            }
            int min;
            if (list1Value == null) {
                min = list2Value;
                list2 = list2.next;
            } else if (list2Value == null) {
                min = list1Value;
                list1 = list1.next;
            } else if (list1Value <= list2Value) {
                min = list1Value;
                list1 = list1.next;
            } else {
                min = list2Value;
                list2 = list2.next;
            }
            if (temp == null) {
                temp = new ListNode(min);
                result = temp;
            } else {
                temp.next = new ListNode(min);
                temp = temp.next;
            }
        }
        return result;
    }

    public static void main(String[] args) {

        L_21 l22 = new L_21();
        {
            ListNode l1 = new ListNode();
            l1.val = 1;
            l1.next = new ListNode(2);
            l1.next.next = new ListNode(4);

            ListNode l2 = new ListNode();
            l2.val = 1;
            l2.next = new ListNode(3);
            l2.next.next = new ListNode(4);
            println(l22.mergeTwoLists(l1, l2));
        }
        {
            ListNode l1 = new ListNode();
            ListNode l2 = new ListNode();
            println(l22.mergeTwoLists(null, null));
        }
        {
            ListNode l1 = new ListNode();

            ListNode l2 = new ListNode();
            l2.val = 0;
            println(l22.mergeTwoLists(null, l2));
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
