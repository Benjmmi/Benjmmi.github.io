package top100liked;

public class L_23 {
    public ListNode mergeKLists(ListNode[] lists) {
        ListNode dummy = new ListNode(0);
        ListNode temp = dummy;
        while (true){
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < lists.length; i++) {
                if (max == Integer.MIN_VALUE && lists[i] != null) {
                    max = i;
                    continue;
                }
                if (lists[i] != null && lists[max].val > lists[i].val) {
                    max = i;
                }
            }
            if (max == Integer.MIN_VALUE) {
                break;
            }
            temp.next = lists[max];
            temp = temp.next;
            lists[max] = lists[max].next;
        }
        return dummy.next;
    }

    public static void main(String[] args) {

    }
}
