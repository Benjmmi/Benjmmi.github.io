package top100liked;

public class L_108 {
    public TreeNode sortedArrayToBST(int[] nums) {

        return insert(nums, 0, nums.length - 1);
    }

    public TreeNode insert(int[] nums, int left, int right) {
        if (left > right) {
            return null;
        }
        int mid = (left + right) / 2;
        TreeNode root = new TreeNode(nums[mid]);
        root.left = insert(nums, left, mid - 1);
        root.right = insert(nums, mid + 1, right);

        return root;
    }


    public void insert(TreeNode root, int val) {
        if (root.val > val) {
            if (root.left == null) {
                root.left = new TreeNode(val);
            } else {
                insert(root.left, val);
            }
        }
        if (root.val < val) {
            if (root.right == null) {
                root.right = new TreeNode(val);
            } else {
                insert(root.right, val);
            }
        }
    }

    public static void main(String[] args) {

    }
}
