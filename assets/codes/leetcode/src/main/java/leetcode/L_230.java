package leetcode;

public class L_230 {


    int min;
    public static int index = 0;

    public int kthSmallest(TreeNode root, int k) {
        dfs(root, k);
        index = 0;
        return min;
    }

    public void dfs(TreeNode root, int k) {
        if (root == null) {
            return;
        }
        dfs(root.left, k);
        ++index;
        if (index == k) {
            min = root.val;
        }
        System.out.println("index=" + index + ",val=" + root.val);
        dfs(root.right, k);
    }


    public static void main(String[] args) {

    }
}
