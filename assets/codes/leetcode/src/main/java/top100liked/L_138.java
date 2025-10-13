package top100liked;

import java.util.HashMap;
import java.util.Map;

public class L_138 {
    public Node copyRandomList(Node head) {
        Node dummy = new Node(0);
        Node temp = dummy;
        Node cur = head;
        Map<Node, Node> nodeMap = new HashMap<>();
        while (cur != null) {
            temp.next = new Node(cur.val);
            nodeMap.put(cur, temp.next);
            cur = cur.next;
            temp = temp.next;
        }

        cur = head;
        temp = dummy.next;
        while (cur != null) {
            if (cur.random != null) {
                temp.random = nodeMap.get(cur.random);
            }
            cur = cur.next;
            temp = temp.next;
        }

        return dummy.next;
    }

    public static void main(String[] args) {

    }
}

class Node {
    int val;
    Node next;
    Node random;

    public Node(int val) {
        this.val = val;
        this.next = null;
        this.random = null;
    }
}
