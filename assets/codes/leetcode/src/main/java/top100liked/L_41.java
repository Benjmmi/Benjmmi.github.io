package top100liked;

import java.util.Arrays;

public class L_41 {
    public int firstMissingPositive(int[] nums) {
        Arrays.sort(nums);
        int i = 0;
        int next = 1;
        while (i < nums.length) {
            if (nums[i] <= 0) {
                i++;
                continue;
            }

            if (next == -1) {
                next = nums[i] + 1;
            } else if (nums[i] != next) {
                break;
            }
            while (i + 1 < nums.length && nums[i] == nums[i + 1]) {
                i++;
            }
            next++;
            i++;
        }
        return next;
    }

    public static void main(String[] args) {
        L_41 l41 = new L_41();
        System.out.println(l41.firstMissingPositive(new int[]{1, 2, 0}));
        System.out.println(l41.firstMissingPositive(new int[]{3, 4, -1, 1}));
        System.out.println(l41.firstMissingPositive(new int[]{7, 8, 9, 11, 12}));
    }
}
