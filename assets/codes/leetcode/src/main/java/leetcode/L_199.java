package leetcode;

import java.util.*;
import java.util.stream.Collectors;

public class L_199 {

    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> integers = new ArrayList<>();
        dsf(root, 0, integers);
        return integers;
    }
    /*
    {
        if (root == null) {
            return new ArrayList<>();
        }
        List<Integer> result = new ArrayList<>();
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 1; i <= size; i++) {
                TreeNode val = queue.poll();
                assert val != null;
                if (val.right != null) {
                    queue.offer(val.right);
                }
                if (val.left != null) {
                    queue.offer(val.left);
                }
                if (i == 1) {
                    result.add(val.val);
                }
            }
        }
        return result;
    }
    */

    public void dsf(TreeNode root, int deep, List<Integer> abs) {
        if (root == null) {
            return;
        }
        if (deep == abs.size()) {
            abs.add(root.val);
        }
        dsf(root.right, deep + 1, abs);
        dsf(root.left, deep + 1, abs);
    }

    public static void main(String[] args) {

    }
}
