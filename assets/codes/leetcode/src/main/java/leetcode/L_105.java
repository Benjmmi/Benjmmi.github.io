package leetcode;

import java.util.HashMap;
import java.util.Map;

public class L_105 {
    Map<Integer, Integer> map = new HashMap<>();
    int[] preorder;
    int[] inorder;

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        this.preorder = preorder;
        this.inorder = inorder;

        for (int index = 0; index < preorder.length; index++) {
            map.put(inorder[index], index);
        }

        return build(0, 0, preorder.length - 1);
//        return build2(0, preorder.length, 0, inorder.length);
    }

    /**
     *
     * @param root 根节点索引
     * @param left 左子树起始位置
     * @param right 右子树起始位置
     * @return
     */
    public TreeNode build(int root, int left, int right) {
        if (left > right) {
            return null;
        }
        TreeNode node = new TreeNode(preorder[root]);
        int index = map.get(preorder[root]);
        node.left = build(root + 1, left, index - 1);
        /**
         * index 是当前根节点在中序遍历中的索引。
         * left 是当前中序遍历的左边界。
         * index - left 计算出当前根节点左子树的节点个数。
         * 因此，root + i - left + 1 表示跳过当前根节点和它左子树的节点，指向右子树的根节点。
         */
        node.right = build(root + (index - left) + 1, index + 1, right);
        return node;
    }

//    public TreeNode build2(int p_start, int p_end, int i_start, int i_end) {
//        if (p_start == p_end) {
//            return null;
//        }
//        // 根节点
//        TreeNode node = new TreeNode(preorder[p_start]);
//        // 根据中序遍历获取到根节点的位置
//        int index = map.get(node.val);
//        // 左节点数量
//        int left_number = i_start - index;
//        // 左边就是左节点
//        node.left = build2(p_start + 1, p_start + left_number + 1, i_start, index);
//        // 右边就是右节点
//        node.right = build2(p_start + left_number + 1, p_end, index + 1, i_end);
//        return node;
//    }

    public static void main(String[] args) {
        L_105 l105 = new L_105();
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
