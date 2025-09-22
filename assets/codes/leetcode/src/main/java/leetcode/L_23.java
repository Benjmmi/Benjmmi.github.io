package leetcode;

import java.util.*;

public class L_23 {
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists.length == 0) {
            return new ListNode();
        }
        ListNode dummyNode = new ListNode();
        ListNode tmp = dummyNode;
        Map<ListNode, ListNode> map = new HashMap<>();
        for (ListNode list : lists) {
            if (list != null) {
                map.put(list, list);
            }
        }
        while (!map.isEmpty()) {
            ListNode min = minNode(map);
            tmp.next = min;
            tmp = tmp.next;
        }
        return dummyNode.next;
    }

    public ListNode minNode(Map<ListNode, ListNode> map) {
        ListNode min = new ListNode(Integer.MAX_VALUE);
        Iterator<ListNode> iterator = map.values().iterator();
        while (iterator.hasNext()) {
            ListNode listNode = iterator.next();
            if (listNode == null) {
                iterator.remove();
                continue;
            }
            if (listNode.val < min.val) {
                min = listNode;
            }
        }
        map.remove(min);
        if (min.next != null) {
            map.put(min.next, min.next);
        }
        return min;
    }


    public ListNode mergeNodeList(ListNode node1, ListNode node2) {
        ListNode dummyNode = new ListNode();
        ListNode tmp = dummyNode;
        while (node1 != null && node2 != null) {
            if (node1.val > node2.val) {
                tmp.next = node2;
                node2 = node2.next;
            } else {
                tmp.next = node1;
                node1 = node1.next;
            }
        }
        tmp.next = node1 == null ? node2 : node1;
        return dummyNode.next;
    }


    public static void main(String[] args) {

    }
}
