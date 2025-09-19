package leetcode;

import java.util.ArrayList;
import java.util.List;

public class L_1382 {
    public TreeNode balanceBST(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        midOrder(root, list);
        Integer[] ints = new Integer[list.size()];
        ints = list.toArray(ints);
        return dfs(ints, 0, ints.length - 1);
    }

    public void midOrder(TreeNode root, List<Integer> list) {
        if (root == null) {
            return;
        }
        midOrder(root.left, list);
        list.add(root.val);
        midOrder(root.right, list);
    }

    public TreeNode dfs(Integer[] nums, int left, int right) {
        if (left > right) {
            return null;
        }
        int mid = (left + right) / 2;
        TreeNode root = new TreeNode(nums[mid]);
        root.left = dfs(nums, left, mid - 1);
        root.right = dfs(nums, mid + 1, right);
        return root;
    }
}
