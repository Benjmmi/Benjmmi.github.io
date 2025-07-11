package leetcode;

/**
 * 给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。
 *
 *
 *
 * 示例 1：
 *
 *
 *
 * 输入：height = [0,1,0,2,1,0,1,3,2,1,2,1]
 * 输出：6
 * 解释：上面是由数组 [0,1,0,2,1,0,1,3,2,1,2,1] 表示的高度图，在这种情况下，可以接 6 个单位的雨水（蓝色部分表示雨水）。
 * 示例 2：
 *
 * 输入：height = [4,2,0,3,2,5]
 * 输出：9
 *
 *
 * 提示：
 *
 * n == height.length
 * 1 <= n <= 2 * 104
 * 0 <= height[i] <= 105
 */
public class L_42 {

    public int trap(int[] height) {
        // 先考虑正向的算法
        // 当前值取代表最大值，直到遇到更大值
        // 思路，判断当前首个值为最大值，往后面找的就只是可容量大小
        // 双指针只能解决，可靠范围内每次都遇到最大值的情况
        // 但是正对两边无法遇到最大值时无法准确的获取
        // 需要解决尾部可接雨水量
        // 遇不到比自己更大值时，回测？

        int preMax = 0;
        int preMaxMark = 0;
        int sum = 0;
        int tempSum = 0;
        for (int i = 0; i < height.length; i++) {
            if (height[i] >= preMax) {
                sum += tempSum;
                preMax = height[i];
                tempSum = 0;
                preMaxMark = i;
            } else {
                tempSum += preMax - height[i];
            }
        }

        preMax = 0;
        tempSum = 0;
        for (int i = height.length - 1; i >= preMaxMark; i--) {
            if (height[i] > preMax) {
                sum += tempSum;
                preMax = height[i];
                tempSum = 0;
            } else {
                tempSum += preMax - height[i];
            }
        }


        return sum;
    }

    public static void main(String[] args) {
        L_42 l42 = new L_42();
        {
            System.out.println(l42.trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}));// 6
        }
        {
            System.out.println(l42.trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 1, 2, 1}));
        }
        {
            System.out.println(l42.trap(new int[]{4, 2, 0, 3, 2, 5})); // 9
        }
        {
            System.out.println(l42.trap(new int[]{2,0,2})); // 2
        }
    }

}
