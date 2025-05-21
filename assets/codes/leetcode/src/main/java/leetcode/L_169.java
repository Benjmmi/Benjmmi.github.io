package leetcode;

import java.util.Arrays;
import java.util.Random;

/**
 * {@link https://leetcode.cn/problems/majority-element/description/?envType=study-plan-v2&envId=top-interview-150}
 *
 * 给定一个大小为 n 的数组 nums ，返回其中的多数元素。多数元素是指在数组中出现次数 大于 ⌊ n/2 ⌋ 的元素。
 *
 * 你可以假设数组是非空的，并且给定的数组总是存在多数元素。
 *
 *
 *
 * 示例 1：
 *
 * 输入：nums = [3,2,3]
 * 输出：3
 * 示例 2：
 *
 * 输入：nums = [2,2,1,1,1,2,2]
 * 输出：2
 *
 *
 * 提示：
 * n == nums.length
 * 1 <= n <= 5 * 104
 * -109 <= nums[i] <= 109
 *
 *
 * 进阶：尝试设计时间复杂度为 O(n)、空间复杂度为 O(1) 的算法解决此问题。
 */
public class L_169 {
    public int majorityElement(int[] nums) {
        // 不用 hashmap，不用 统计方式
        //
        // VOTE
        int num = nums[0];
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            if (count == 0) {
                num = nums[i];
            }
            if (nums[i] == num) {
                count++;
            } else {
                count--;
            }
        }

        return num;
    }

    public static void main(String[] args) {
        L_169 l_169 = new L_169();
        {
            int[] nums = {3, 2, 3}; // 3
            System.out.println(l_169.majorityElement(nums));
        }
        {
            int[] nums = {2, 2, 1, 1, 1, 2, 2}; // 2
            System.out.println(l_169.majorityElement(nums));
        }
        {
            int[] nums = {2, 1, 1, 1, 1, 2, 2}; // 1
            System.out.println(l_169.majorityElement(nums));
        }
        {
            int[] nums = {2, 2, 3, 1, 1, 1, 1}; // 1
            System.out.println(l_169.majorityElement(nums));
        }
        {
            int[] nums = {3, 2, 3, 3, 1, 3, 1}; // 3
            System.out.println(l_169.majorityElement(nums));
        }
    }
}
