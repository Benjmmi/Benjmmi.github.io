package leetcode;

public class L_98 {
    long pre = Long.MIN_VALUE;
    boolean b = true;

    public boolean isValidBST(TreeNode root) {
        dfs(root);
        return b;
    }

    public void dfs(TreeNode root) {
        if (root == null) {
            return;
        }
        dfs(root.left);
        if (root.val > pre) {
            pre = root.val;
        } else {
            b = false;
            return;
        }
        dfs(root.right);
    }

    public static void main(String[] args) {
        L_98 l98 = new L_98();
        {
            TreeNode treeNode = new TreeNode(-2147483648);
            System.out.println(l98.isValidBST(treeNode));
        }
        {
            TreeNode treeNode = new TreeNode(2);
            treeNode.left = new TreeNode(1);

            treeNode.right = new TreeNode(3);
            System.out.println(l98.isValidBST(treeNode));
        }
        {
            TreeNode treeNode = new TreeNode(5);
            treeNode.left = new TreeNode(1);

            treeNode.right = new TreeNode(4);
            treeNode.right.left = new TreeNode(3);
            treeNode.right.right = new TreeNode(6);
            System.out.println(l98.isValidBST(treeNode));
        }
    }
}
