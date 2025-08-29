package leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class L_106 {

    Map<Integer, Integer> map = new HashMap<>();
    int[] inorder;
    int[] postorder;

    public TreeNode buildTree(int[] inorder, int[] postorder) {
        this.inorder = inorder;
        this.postorder = postorder;
        for (int i = 0; i < inorder.length; i++) {
            map.put(inorder[i], i);
        }
        return build(0, inorder.length - 1, 0, postorder.length - 1);
    }

    public TreeNode build(int in_start, int in_end, int post_start, int post_end) {
        if (in_end < in_start || post_end < post_start) return null;

        int root = postorder[post_end];
        int root_index = map.get(root);

        TreeNode node = new TreeNode(root);
        node.left = build(in_start, root_index - 1, post_start, post_start + root_index - in_start - 1);
        node.right = build(root_index + 1, in_end, post_start + root_index - in_start, post_end - 1);
        return node;
    }

    public TreeNode build2(int[] inorder, int[] postorder) {

        int n = postorder.length;
        if (n == 0) {
            return null;
        }
        int root = postorder[n - 1];

        int root_index = map.get(root);
        int[] in_left = Arrays.copyOfRange(inorder, 0, root_index);
        int[] in_right = Arrays.copyOfRange(inorder, root_index + 1, n);

        int[] post_left = Arrays.copyOfRange(postorder, 0, root_index);
        int[] post_right = Arrays.copyOfRange(postorder, root_index, n - 1);

        TreeNode left = build2(in_left, post_left);
        TreeNode right = build2(in_right, post_right);
        return new TreeNode(root, left, right);
    }


    // build3(0,postorder.length,0,postorder.length)
    public TreeNode build3(int inL, int inR, int postL, int postR) {
        if (postL == postR) {
            return null;
        }

        int leftSize = map.get(postorder[postR - 1]) - inL;

        TreeNode left = build3(inL, inL + leftSize, postL, postL + leftSize);
        TreeNode right = build3(inL + leftSize + 1, inR, postL + leftSize, postR - 1);

        return new TreeNode(postorder[postR - 1], left, right);
    }


    public static void main(String[] args) {
        L_106 l105 = new L_106();
        {
            println(l105.buildTree(new int[]{3, 9, 20, 15, 7}, new int[]{9, 3, 15, 20, 7}));
            System.out.println();
        }

    }

    public static void println(TreeNode root) {
        if (root == null) {
            return;
        }
        System.out.print(root.val + ",");
        println(root.left);
        println(root.right);
    }
}
