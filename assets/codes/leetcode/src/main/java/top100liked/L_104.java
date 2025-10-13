package top100liked;

public class L_104 {
    int max = 0;

    public int maxDepth(TreeNode root) {
        maxDepth(root, 1);
        return max;
    }

    public void maxDepth(TreeNode root, int depth) {
        if (root == null) {
            return;
        }
        if (depth > max) {
            max = depth;
        }
        maxDepth(root.left, depth + 1);
        maxDepth(root.right, depth + 1);
    }

}
