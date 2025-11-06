package contest;

import java.util.Arrays;

public class L_Q2 {
    public long maxAlternatingSum(int[] nums) {
        long sum = 0;
        for (int i = 0; i < nums.length; i++) {
            nums[i] = nums[i] * nums[i];
        }
        Arrays.sort(nums);
        for (int i = nums.length - 1; i >= nums.length / 2; i--) {
            sum += nums[i];
        }
        for (int i = nums.length / 2 - 1; i >= 0; i--) {
            sum -= nums[i];
        }
        return sum;
    }

    public static void main(String[] args) {
        L_Q2 lq = new L_Q2();
        System.out.println(lq.maxAlternatingSum(new int[]{1, 2, 3}));
        System.out.println(lq.maxAlternatingSum(new int[]{1, -1, 2, -2, 3, -3}));
    }
}
