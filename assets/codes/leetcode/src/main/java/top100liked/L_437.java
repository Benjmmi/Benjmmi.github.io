package top100liked;

public class L_437 {

    public int pathSum(TreeNode root, int targetSum) {
        // 如果整棵树为空，直接返回 0
        if (root == null) {
            return 0;
        }
        int pathsFromRoot = countPathsSum(root, targetSum);

        int pathsOnLeft = pathSum(root.left, targetSum);

        int pathsOnRight = pathSum(root.right, targetSum);


        return pathsFromRoot + pathsOnLeft + pathsOnRight;
    }


    public int countPathsSum(TreeNode root, long targetSum) {
        if (root == null) return 0;
        int count = (root.val == targetSum) ? 1 : 0;

        count += countPathsSum(root.left, targetSum - root.val);

        count += countPathsSum(root.right, targetSum - root.val);


        return count;

    }

    public static void main(String[] args) {
        L_437 l437 = new L_437();
        TreeNode root = new TreeNode(10);
        root.left = new TreeNode(5);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(2);
        root.left.right.right = new TreeNode(1);

        root.left.left.left = new TreeNode(3);
        root.left.left.right = new TreeNode(-2);

        root.right = new TreeNode(-3);
        root.right.right = new TreeNode(11);
        System.out.println(l437.pathSum(root, 8));

    }
}
