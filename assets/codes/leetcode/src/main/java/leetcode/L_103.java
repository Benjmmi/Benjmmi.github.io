package leetcode;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class L_103 {

    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        if (root == null) {
            return new ArrayList<>();
        }
        // false  left 从左往右遍历
        // true   right 从右往左遍历
        boolean guide = true;
        ArrayDeque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        List<List<Integer>> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> values = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                values.add(node.val);
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
                if (!guide) {
                    Collections.reverse(values);
                }
            }
            System.out.println();
            guide = !guide;
            result.add(values);
        }
        return result;
    }

    public static void main(String[] args) {

        L_103 l103 = new L_103();
        {
            TreeNode treeNode = new TreeNode(1);
            treeNode.left = new TreeNode(2);
            treeNode.left.left = new TreeNode(4);

            treeNode.right = new TreeNode(3);
            treeNode.right.right = new TreeNode(5);
            System.out.println(l103.zigzagLevelOrder(treeNode));
        }

    }
}
