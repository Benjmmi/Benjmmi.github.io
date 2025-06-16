package leetcode;

/**
 * {@see http://leetcode.cn/problems/jump-game/?envType=study-plan-v2&envId=top-interview-150}
 * 给你一个非负整数数组 nums ，你最初位于数组的 第一个下标 。数组中的每个元素代表你在该位置可以跳跃的最大长度。
 *
 * 判断你是否能够到达最后一个下标，如果可以，返回 true ；否则，返回 false 。
 *
 *
 *
 * 示例 1：
 *
 * 输入：nums = [2,3,1,1,4]
 * 输出：true
 * 解释：可以先跳 1 步，从下标 0 到达下标 1, 然后再从下标 1 跳 3 步到达最后一个下标。
 * 示例 2：
 *
 * 输入：nums = [3,2,1,0,4]
 * 输出：false
 * 解释：无论怎样，总会到达下标为 3 的位置。但该下标的最大跳跃长度是 0 ， 所以永远不可能到达最后一个下标。
 *
 *
 * 提示：
 *
 * 1 <= nums.length <= 104
 * 0 <= nums[i] <= 105
 */
public class L_55 {
    public boolean canJump(int[] nums) {
        // 单指针不行
//        int i = 0;
//        while (true) {
//            if (i >= nums.length - 1) {
//                return true;
//            }
//            if (nums[i] == 0) {
//                break;
//            }
//            i += nums[i];
//        }
//        return false;
        // 暴力破解 需要 2n
        return false;
    }

    public static void main(String[] args) {
        L_55 l55 = new L_55();
        {
            int[] nums = {2, 3, 1, 1, 4}; // true
            System.out.println(l55.canJump(nums));
        }
        {
            int[] nums = {3, 2, 1, 0, 4}; // false
            System.out.println(l55.canJump(nums));
        }
        {
            int[] nums = {3, 2, 1, 1, 0}; // false
            System.out.println(l55.canJump(nums));
        }
        {
            int[] nums = {3, 2, 1, 0, 0}; // false
            System.out.println(l55.canJump(nums));
        }
        {
            int[] nums = {0, 2, 1, 0, 0}; // false
            System.out.println(l55.canJump(nums));
        }
        {
            int[] nums = {1, 1, 1, 1, 0}; // false
            System.out.println(l55.canJump(nums));
        }
        {
            int[] nums = {2, 5, 0, 0}; // false
            System.out.println(l55.canJump(nums));
        }
        {
            int[] nums = {5, 5, 0, 0}; // false
            System.out.println(l55.canJump(nums));
        }
        {
            int[] nums = {4, 1, 3, 3, 0, 0, 5, 0, 5, 2, 0, 0}; // false
            System.out.println(l55.canJump(nums));
        }
        {
            int[] nums = {4, 1, 3, 3, 0, 0, 5, 0, 5, 2, 0, 0}; // false
            System.out.println(l55.canJump(nums));
        }
    }
}
