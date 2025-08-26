package leetcode;

public class L_101 {

    public boolean isSymmetric(TreeNode root) {
        if (root == null) {
            return true;
        }

        return check(root.left, root.right);
    }

    public boolean check(TreeNode l, TreeNode r) {
        if (l == null && r == null) {
            return true;
        }
        if (l == null || r == null) {
            return false;
        }
        return l.val == r.val && check(l.left, r.right) && check(l.right, r.left);

    }

    public static void main(String[] args) {
        L_101 l101 = new L_101();

    }
}
