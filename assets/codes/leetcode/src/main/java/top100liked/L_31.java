package top100liked;

import java.util.Arrays;

public class L_31 {

    public void nextPermutation(int[] nums) {
        boolean hasChanged = false;
        int max = Integer.MIN_VALUE;
        int maxIndex = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i - 1] < nums[i] && nums[i] > max) {
                max = nums[i];
                maxIndex = i;
            }
        }
        if (max != Integer.MIN_VALUE) {
            hasChanged = true;
            int a = nums[maxIndex - 1];
            nums[maxIndex - 1] = nums[maxIndex];
            nums[maxIndex] = a;
        }
        if (hasChanged) {
            return;
        }
        Arrays.sort(nums);
    }

    public static void main(String[] args) {
        L_31 l31 = new L_31();
        l31.nextPermutation(new int[]{1, 2, 3});
        l31.nextPermutation(new int[]{3, 2, 1});
        l31.nextPermutation(new int[]{1, 1, 5});

    }
}
