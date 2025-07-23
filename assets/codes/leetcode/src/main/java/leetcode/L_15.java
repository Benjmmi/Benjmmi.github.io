package leetcode;

import java.util.*;

/**
 * 给你一个整数数组 nums ，判断是否存在三元组 [nums[i], nums[j], nums[k]] 满足 i != j、i != k 且 j != k ，同时还满足 nums[i] + nums[j] + nums[k] == 0 。请你返回所有和为 0 且不重复的三元组。
 *
 * 注意：答案中不可以包含重复的三元组。
 *
 *
 *
 *
 *
 * 示例 1：
 *
 * 输入：nums = [-1,0,1,2,-1,-4]
 * 输出：[[-1,-1,2],[-1,0,1]]
 * 解释：
 * nums[0] + nums[1] + nums[2] = (-1) + 0 + 1 = 0 。
 * nums[1] + nums[2] + nums[4] = 0 + 1 + (-1) = 0 。
 * nums[0] + nums[3] + nums[4] = (-1) + 2 + (-1) = 0 。
 * 不同的三元组是 [-1,0,1] 和 [-1,-1,2] 。
 * 注意，输出的顺序和三元组的顺序并不重要。
 * 示例 2：
 *
 * 输入：nums = [0,1,1]
 * 输出：[]
 * 解释：唯一可能的三元组和不为 0 。
 * 示例 3：
 *
 * 输入：nums = [0,0,0]
 * 输出：[[0,0,0]]
 * 解释：唯一可能的三元组和为 0 。
 *
 *
 * 提示：
 *
 * 3 <= nums.length <= 3000
 * -105 <= nums[i] <= 105
 */
public class L_15 {


    public List<List<Integer>> threeSum(int[] nums) {
        // 如果没有顺序要求，那做个排序即可
        // 遍历截止到第一位数截止到 第一个正数结束
        // 如果全是负数也不行,还有考虑 全是 0 的情况
        List<List<Integer>> list = new ArrayList<>();

        Arrays.sort(nums);

        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > 0) {
                break;
            }

            int left = i + 1;
            int right = nums.length - 1;
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            while (left < right) {
                int answer = nums[i] + nums[left] + nums[right];
                if (answer == 0) {
                    list.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    while (left < right && nums[left] == nums[left + 1]) {
                        left++;
                    }
                    while (left < right && nums[right] == nums[right - 1]) {
                        right--;
                    }
                    left++;
                    right--;
                } else if (answer > 0) {
                    right--;
                } else {
                    // answer < 0
                    left++;
                }
            }

        }


        return list;
    }

//    public List<List<Integer>> threeSum(int[] nums) {
//
//        List<List<Integer>> list = new ArrayList<>();
//        Set<String> c = new HashSet<>();
//        for (int i = 0; i < nums.length - 2; i++) {
//            for (int j = i + 1; j < nums.length - 1; j++) {
//                for (int k = j + 1; k < nums.length; k++) {
//                    List<Integer> list1 = new ArrayList<>();
//                    list1.add(nums[i]);
//                    list1.add(nums[j]);
//                    list1.add(nums[k]);
//                    list1.sort(new Comparator<Integer>() {
//                        @Override
//                        public int compare(Integer o1, Integer o2) {
//                            return o1 - o2;
//                        }
//                    });
//                    String val = String.join("_", String.valueOf(list1.get(0)), String.valueOf(list1.get(1)), String.valueOf(list1.get(2)));
//                    if (c.add(val) && (nums[i] + nums[j] + nums[k]) == 0) {
//                        list.add(list1);
//                    }
//                }
//            }
//        }
//
//        return list;
//    }

    public static void main(String[] args) {
        L_15 l15 = new L_15();
        {
            System.out.println(l15.threeSum(new int[]{-1, 0, 1, 2, -1, -4}));
        }
        {
            System.out.println(l15.threeSum(new int[]{0, 1, 1}));
        }
        {
            System.out.println(l15.threeSum(new int[]{0, 0, 0}));
        }
        {
            System.out.println(l15.threeSum(new int[]{10, -2, -12, 3, -15, -12, 2, -11, 3, -12, 9, 12, 0, -5, -4, -2, -7, -15, 7, 4, -5, -14, -15, -15, -4, 10, 9, -6, 7, 1, 12, -6, 14, -15, 12, 14, 10, 0, 10, -10, 3, 4, -12, 10, 7, -9, -7, -15, -8, -15, -4, 2, 9, -4, 3, -10, 13, -3, -1, 5, 5, -4, -15, 4, -11, 4, -4, 6, -11, -9, 12, 7, 11, 7, 2, -5, 13, 10, -5, -10, 3, 7, 0, -3, 10, -12, 2, 9, -8, 8, -9, 13, 12, 13, -6, 8, 3, 5, -9, 7, 12, 10, -8, 0, 2, 1, 10, -7, -3, -10, -10, 7, 4, 5, -11, -8, 0, -2, -14, 8, 13, -8, -2, 10, 13}));
        }
    }
}
