package leetcode;

public class L_108 {

    public TreeNode sortedArrayToBST(int[] nums) {
        return search(nums, 0, nums.length - 1);
    }

    public TreeNode search(int[] nums, int left, int right) {
        if (left > right) {
            return null;
        }
        int mid = (left + right) / 2;
        TreeNode root = new TreeNode(nums[mid]);
        root.left = search(nums,left,mid-1);
        root.right = search(nums,mid+1,right);
        return root;
    }

    public static void main(String[] args) {
        L_108 l108 = new L_108();
        {
            TreeNode node = l108.sortedArrayToBST(new int[]{-10, -3, 0, 5, 9});
            System.out.println(node);
        }
    }
}
