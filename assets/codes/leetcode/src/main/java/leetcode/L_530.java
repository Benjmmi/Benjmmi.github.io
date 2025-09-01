package leetcode;

import java.util.*;

public class L_530 {
    private int ans = Integer.MAX_VALUE;
    private int pre = Integer.MIN_VALUE / 2;


    public int getMinimumDifference(TreeNode root) {
        if (root == null) {
            return 0;
        }
        dsf(root);
        return ans;
    }

    public void dsf(TreeNode root) {
        if (root == null) {
            return;
        }
        dsf(root.left);
        ans = Math.min(ans, root.val - pre);
        pre = root.val;
        dsf(root.right);
    }

    public static void main(String[] args) {
        L_530 l530 = new L_530();
        {
            TreeNode treeNode = new TreeNode(543);
            treeNode.left = new TreeNode(384);
            treeNode.left.right = new TreeNode(445);

            treeNode.right = new TreeNode(652);
            treeNode.right.right = new TreeNode(699);
            System.out.println(l530.getMinimumDifference(treeNode));
        }
        {
            TreeNode treeNode = new TreeNode(1);
            treeNode.right = new TreeNode(3);
            treeNode.right.right = new TreeNode(2);
            System.out.println(l530.getMinimumDifference(treeNode));
        }

        {
            TreeNode treeNode = new TreeNode(4);
            treeNode.left = new TreeNode(2);
            treeNode.left.left = new TreeNode(1);
            treeNode.left.right = new TreeNode(3);

            treeNode.right = new TreeNode(6);
            System.out.println(l530.getMinimumDifference(treeNode));
            ;
        }
    }
}
