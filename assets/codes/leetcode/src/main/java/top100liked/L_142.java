package top100liked;

import java.util.HashMap;
import java.util.Map;

public class L_142 {
    public ListNode detectCycle(ListNode head) {
        Map<ListNode, Integer> map = new HashMap<>();
        int index = 0;
        while (head != null) {
            if (map.containsKey(head)) {
                return head;
            }
            map.put(head, index);
            head = head.next;
            index++;
        }
        return null;
    }

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
    }
}
