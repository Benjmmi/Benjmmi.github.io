package top100liked;

import java.util.ArrayList;
import java.util.List;

public class L_98 {

    public boolean isValidBST(TreeNode root) {
        if (root == null) return true;
//        List<Integer> integers = new ArrayList<>();
//        preOrder(root, integers);
//        int preValue = integers.get(0);
//        for (int i = 1; i < integers.size(); i++) {
//            if (preValue < integers.get(i)) {
//                preValue = integers.get(i);
//            } else {
//                return false;
//            }
//        }

        preOrder(root, new ArrayList<>(), Integer.MIN_VALUE);

        return true;
    }

    public boolean preOrder(TreeNode root, List<Integer> integers, Integer preValue) {
        if (root == null) return true;
        boolean b = preOrder(root.left, integers, root.val);
        if (!b) return false;
        if (root.val <= preValue) {
            return false;
        }
        boolean c = preOrder(root.right, integers, root.val);
        if (!c) return false;
        return true;
    }

    public static void main(String[] args) {

    }
}
