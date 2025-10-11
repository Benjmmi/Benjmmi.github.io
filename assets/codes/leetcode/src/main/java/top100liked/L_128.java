package top100liked;

import java.util.Arrays;

public class L_128 {
    public int longestConsecutive(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        Arrays.sort(nums);


        int max = 1;

        int cur = 1;

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] - nums[i - 1] == 1) {
                cur++;
            } else if (nums[i] - nums[i - 1] == 0) {
                continue;
            } else {
                cur = 1;
            }
            max = Math.max(max, cur);
        }

        return max;
    }

    public static void main(String[] args) {

    }
}
