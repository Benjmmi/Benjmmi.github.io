package top100liked;

public class L_543 {
    public int diameterOfBinaryTree(TreeNode root) {
        if (root == null) return 0;
        dsf(root);
        return max;
    }

    int max = Integer.MIN_VALUE;

    public int dsf(TreeNode root) {
        if (root == null) {
            return -1;
        }
        int left = 1 + dsf(root.left);
        int right = 1 + dsf(root.right);

        if (left + right > max) {
            max = left + right;
        }

        return Math.max(left, right);
    }

    public static void main(String[] args) {
        L_543 l543 = new L_543();
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        System.out.println(l543.diameterOfBinaryTree(root));
    }
}
