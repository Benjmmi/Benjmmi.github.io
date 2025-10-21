package top100liked;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class L_199 {

    public List<Integer> rightSideView(TreeNode root) {
        if (root == null) return new ArrayList<>();
        List<Integer> res = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            boolean a = false;
            for (int i = 0; i < size; i++) {
                TreeNode val = queue.poll();
                if (i == size - 1) {
                    res.add(val.val);
                }
                if (val.left != null) {
                    queue.add(val.left);
                }
                if (val.right != null) {
                    queue.add(val.right);
                }
            }
        }

        return res;
    }


    public static void main(String[] args) {

    }
}
