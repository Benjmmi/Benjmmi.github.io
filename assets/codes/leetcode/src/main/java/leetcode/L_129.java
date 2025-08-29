package leetcode;

public class L_129 {
    int sum = 0;

    public int sumNumbers(TreeNode root) {
        if (root == null) {
            return 0;
        }
        build(root, 0);
        System.out.println(sum);
        return sum;
    }

    public void build(TreeNode root, int s) {
        if (root == null) {
            return;
        }
        if (root.left == null && root.right == null) {
            sum += (s * 10 + root.val);
            return;
        }
        build(root.left, s * 10 + root.val);
        build(root.right, s * 10 + root.val);
    }

    public static void main(String[] args) {
        L_129 l129 = new L_129();
        {
            l129.sum = 0;
            TreeNode root = new TreeNode(1);
            root.left = new TreeNode(2);
            root.right = new TreeNode(3);
            l129.sumNumbers(root);
        }
        {
            l129.sum = 0;
            TreeNode root = new TreeNode(4);
            root.left = new TreeNode(9);
            root.left.left = new TreeNode(5);
            root.left.right = new TreeNode(1);
            root.right = new TreeNode(0);
            l129.sumNumbers(root);
        }
        {
            l129.sum = 0;
            TreeNode root = new TreeNode(4);
            root.right = new TreeNode(0);
            l129.sumNumbers(root);
        }
        {
            l129.sum = 0;
            TreeNode root = new TreeNode(4);
            root.left = new TreeNode(0);
            l129.sumNumbers(root);
        }
    }
}
