package leetcode;

public class L_114 {

    public void flatten(TreeNode root) {
        if (root == null) {
            return;
        }
        TreeNode right = new TreeNode(-1);
        TreeNode temp = right;
        build(root, temp);
        root.right = null;
        root.left = null;
        root.val = right.right.val;
        root.right = right.right.right;
    }

    public void build(TreeNode root, TreeNode right) {
        if (root == null) {
            return;
        }
        while (right.right != null) {
            right = right.right;
        }
        right.right = new TreeNode(root.val);
        build(root.left, right);
        build(root.right, right);
    }


    public void flatten2(TreeNode root) {
        if (root == null) {
            return;
        }

        TreeNode curr = root;
        while (curr != null) {
            if (curr.left != null) {
                TreeNode next = curr.left;
                TreeNode predecessor = next;
                while (predecessor.right != null) {
                    predecessor = predecessor.right;
                }
                predecessor.right = curr.right;
                curr.left = null;
                curr.right = next;
            }
            curr = curr.right;
        }
    }


    public static void main(String[] args) {
        L_114 l114 = new L_114();
        {
            TreeNode node = new TreeNode(1);
            node.left = new TreeNode(2);
            node.left.left = new TreeNode(3);
            node.left.right = new TreeNode(4);
            node.right = new TreeNode(5);
            node.right.right = new TreeNode(6);
            l114.flatten(node);
        }
    }
}
