package leetcode;

import java.util.ArrayDeque;
import java.util.Queue;

public class L_117 {

    class Node {
        public int val;
        public Node left;
        public Node right;
        public Node next;

        public Node() {
        }

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, Node _left, Node _right, Node _next) {
            val = _val;
            left = _left;
            right = _right;
            next = _next;
        }
    }


    public Node connect(Node root) {
        // 层序遍历

        if (root == null) {
            return null;
        }

        Queue<Node> queue = new ArrayDeque<>();

        queue.offer(root);

        while (!queue.isEmpty()) {
            int n = queue.size();
            Node last = null;
            for (int i = 1; i <= n; i++) {
                Node f = queue.poll();
                if (f.left != null) {
                    // 先放左节点
                    queue.offer(f.left);
                }
                if (f.right != null) {
                    // 再放右节点
                    queue.offer(f.right);
                }
                if (i != 1) {
                    // 不是起始节点就执行 左边
                    last.next = f;
                }
                last = f;
            }
        }


        return root;
    }

    public static void main(String[] args) {

    }
}
