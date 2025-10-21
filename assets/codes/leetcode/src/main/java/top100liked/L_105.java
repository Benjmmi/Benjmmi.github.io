package top100liked;

import java.util.HashMap;
import java.util.Map;

public class L_105 {
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            map.put(inorder[i], i);
        }
        return build(preorder, inorder, 0, preorder.length - 1, 0, inorder.length - 1, map);
    }

    public TreeNode build(int[] preorder, int[] inorder, int preStart, int preEnd, int inStart, int inEnd, Map<Integer, Integer> map) {
        if (preStart > preEnd || inStart > inEnd) return null;
        // 创建根节点
        TreeNode root = new TreeNode(preorder[preStart]);
        // 划分左右
        int inRootIndex = map.get(preorder[preStart]);
        int leftSize = inRootIndex - inStart;
        root.left = build(preorder, inorder, preStart + 1, preStart + leftSize, inStart, inRootIndex - 1, map);
        root.right = build(preorder, inorder, preStart + leftSize + 1, preEnd, inRootIndex + 1, inEnd, map);

        return root;
    }

    public static void main(String[] args) {

    }
}
