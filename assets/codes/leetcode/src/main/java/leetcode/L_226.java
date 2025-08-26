package leetcode;

/**
 * 给你一棵二叉树的根节点 root ，翻转这棵二叉树，并返回其根节点。
 *
 *
 */
public class L_226 {
    public TreeNode invertTree(TreeNode root) {
        if (root == null){
            return null;
        }
        // 左节点变右节点
        TreeNode left = invertTree(root.left);
        // 又节点变左节点
        TreeNode right = invertTree(root.right);

        root.left = right;
        root.right = left;

        return root;
    }

    public static void main(String[] args) {
        L_226 l226 = new L_226();
        {

        }
    }
}
