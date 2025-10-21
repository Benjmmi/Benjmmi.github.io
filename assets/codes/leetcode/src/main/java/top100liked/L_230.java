package top100liked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class L_230 {

    public int kthSmallest(TreeNode root, int k) {
        List<Integer> list = new ArrayList<>();
        inOrder(root, list, k);
        Collections.sort(list);
        return list.get(k);
    }

    public void inOrder(TreeNode root, List<Integer> list, int k) {
        if (root == null) return;
        inOrder(root.left, list, k);
        list.add(root.val);
        inOrder(root.right, list, k);
    }

    public static void main(String[] args) {
        L_230 l230 = new L_230();
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(4);
        root.left.left = new TreeNode(3);
        System.out.println(l230.kthSmallest(root, 1));
    }
}
