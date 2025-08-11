package leetcode;

import java.lang.reflect.Array;
import java.util.Arrays;

public class L_128 {
    public int longestConsecutive(int[] nums) {
        Arrays.sort(nums);
        if (nums.length == 0) {
            return 0;
        }
        int max = 1;
        int scope = 1;
        for (int i = 1; i < nums.length; i++) {
            int value = nums[i] - nums[i - 1];
            if (value == 1) {
                scope++;
            } else if (value == 0) {
                continue;
            } else {
                max = Math.max(max, scope);
                scope = 1;
            }
        }
        max = Math.max(max, scope);

        return max;
    }

    public static void main(String[] args) {
        L_128 l128 = new L_128();
        {
            System.out.println(l128.longestConsecutive(new int[]{100, 4, 200, 1, 3, 2}));
        }
        {
            System.out.println(l128.longestConsecutive(new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1}));
        }
        {
            System.out.println(l128.longestConsecutive(new int[]{1, 0, 1, 2}));
        }
        {
            System.out.println(l128.longestConsecutive(new int[]{}));
        }
    }
}
