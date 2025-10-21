package top100liked;

public class L_114 {

    public void flatten(TreeNode root) {
        if (root == null) return;
        TreeNode cur = new TreeNode(root.val);
        this.next = cur;
        preOrder(root);
        root.right = cur.right.right;
        root.left = null;
    }

    private TreeNode next = null;

    public void preOrder(TreeNode root) {
        if (root == null) return;
        next.right = new TreeNode(root.val);
        next = next.right;
        preOrder(root.left);
        preOrder(root.right);
    }

    public static void main(String[] args) {
        L_114 l114 = new L_114();
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(4);
        root.right = new TreeNode(5);
        root.right.right = new TreeNode(6);
        l114.flatten(root);
    }
}
