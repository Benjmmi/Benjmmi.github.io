package top100liked;

import java.util.ArrayList;
import java.util.List;

public class L_124 {
    public int maxPathSum(TreeNode root) {
        if (root == null) return 0;

        int sum = dfs(root);

        return max;
    }

    int max = Integer.MIN_VALUE;

    public int dfs(TreeNode root) {
        if (root == null) return 0;

        int left = Math.max(0, dfs(root.left));

        int right = Math.max(0, dfs(root.right));

        int sum = left + right + root.val;

        max = Math.max(max, sum);

        return root.val + Math.max(left, right);
    }


    public static void main(String[] args) {

        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(-2);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);
        root.left.left.left = new TreeNode(-1);
//
//        root.left.left.left = new TreeNode(3);
//        root.left.left.right = new TreeNode(-2);

        root.right = new TreeNode(-3);
        root.right.right = new TreeNode(-2);
        L_124 l124 = new L_124();
        System.out.println(l124.maxPathSum(root));
    }
}
