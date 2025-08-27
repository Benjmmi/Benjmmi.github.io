package leetcode;

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
        return build(postorder.length - 1, 0, inorder.length - 1);
    }

    public static TreeNode build(int root, int left, int right) {
        if (left > right) {
            return null;
        }


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
