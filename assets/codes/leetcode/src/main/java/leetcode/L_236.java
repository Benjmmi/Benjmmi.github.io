package leetcode;

import java.util.Stack;

public class L_236 {

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {
            return null;
        }



        return root;
    }

    public static void main(String[] args) {
        L_236 l236 = new L_236();
        {
            TreeNode treeNode = new TreeNode(3);
            treeNode.left = new TreeNode(5);
            treeNode.left.left = new TreeNode(6);
            treeNode.left.right = new TreeNode(2);
            treeNode.left.right.left = new TreeNode(7);
            treeNode.left.right.right = new TreeNode(4);

            treeNode.right = new TreeNode(1);
            treeNode.right.left = new TreeNode(0);
            treeNode.right.right = new TreeNode(8);


            l236.lowestCommonAncestor(treeNode, null, null);
        }
    }
}
