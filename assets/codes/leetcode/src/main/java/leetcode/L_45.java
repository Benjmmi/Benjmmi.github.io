package leetcode;

/**
 * 给定一个长度为 n 的 0 索引整数数组 nums。初始位置为 nums[0]。
 *
 * 每个元素 nums[i] 表示从索引 i 向后跳转的最大长度。换句话说，如果你在 nums[i] 处，你可以跳转到任意 nums[i + j] 处:
 *
 * 0 <= j <= nums[i]
 * i + j < n
 * 返回到达 nums[n - 1] 的最小跳跃次数。生成的测试用例可以到达 nums[n - 1]。
 *
 *
 *
 * 示例 1:
 *
 * 输入: nums = [2,3,1,1,4]
 * 输出: 2
 * 解释: 跳到最后一个位置的最小跳跃数是 2。
 *      从下标为 0 跳到下标为 1 的位置，跳 1 步，然后跳 3 步到达数组的最后一个位置。
 * 示例 2:
 *
 * 输入: nums = [2,3,0,1,4]
 * 输出: 2
 *
 *
 * 提示:
 *
 * 1 <= nums.length <= 104
 * 0 <= nums[i] <= 1000
 * 题目保证可以到达 nums[n-1]
 *
 * 捋一下思路
 * 思虑比较简单，
 * 从第一个位置开始，判断第一个可以可以调到的最大范围内的最大值，
 * 如果找到到最大值，如果通过最大值可以到达最后一个位置，则跳出循环，返回次数。
 */
public class L_45 {
    public int jump(int[] nums) {

        int step = 0;
        int end = 0;
        int maxPosition = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            maxPosition = Math.max(maxPosition, i + nums[i]);
            if (end == i) {
                end = maxPosition;
                step++;
            }
        }

        return step;
    }

    public static void main(String[] args) {
        L_45 l45 = new L_45();
        {
            int[] nums = {2, 3, 1, 1, 4};
            System.out.println(l45.jump(nums)); // 输出: 2
        }
        {
            int[] nums = {2, 3, 0, 1, 4};
            System.out.println(l45.jump(nums)); // 输出: 2
        }
        {
            int[] nums = {1, 2, 3};
            System.out.println(l45.jump(nums)); // 输出: 2
        }
        {
            int[] nums = {2, 0, 0};
            System.out.println(l45.jump(nums)); // 输出: 1
        }
    }
}
