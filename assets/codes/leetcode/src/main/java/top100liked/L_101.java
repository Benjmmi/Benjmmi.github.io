package top100liked;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class L_101 {
    public boolean isSymmetric(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int size = 1;
        while (!queue.isEmpty()) {
            List<TreeNode> list = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode treeNode = queue.poll();
                if (treeNode == null){
                    continue;
                }
                list.add(treeNode.left);
                list.add(treeNode.right);
                queue.add(treeNode.left);
                queue.add(treeNode.right);
            }
            size = list.size();
            for (int i = 0; i < size / 2; i++) {
                if (list.get(i) == null && list.get(size - 1 - i) == null) {
                    continue;
                }
                if (list.get(i) == null || list.get(size - 1 - i) == null){
                    return false;
                }
                if (list.get(i).val != list.get(size - 1 - i).val) {
                    return false;
                }
            }
        }
        return true;
    }

}
