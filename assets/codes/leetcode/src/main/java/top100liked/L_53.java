package top100liked;

import java.util.Arrays;

public class L_53 {
    public int maxSubArray(int[] nums) {
//        int sum = 0;
//        for (int i = 0; i < nums.length; i++) {
//            sum += nums[i];
//        }
//        int max = sum;
//        int left = 0;
//        int right = nums.length - 1;
//        while (left < right) {
//            if (nums[left] < nums[right]) {
//                sum -= nums[left];
//                left++;
//            } else {
//                sum -= nums[right];
//                right--;
//            }
//            max = Math.max(max, sum);
//        }
        max = nums[0];

        int preSum = 0;
        int minPreSum = 0;
        for (int i = 0; i < nums.length; i++) {
            preSum += nums[i];
            max = Math.max(max, preSum-minPreSum);
            minPreSum = Math.min(minPreSum, preSum);
        }

//        int n = nums.length;
//        int[][] sum = new int[n][n];
//        for (int i = 0; i < n; i++) {
//            Arrays.fill(sum[i], Integer.MIN_VALUE);
//            sum[i][i] = nums[i];
//            max = Math.max(max, sum[i][i]);
//        }
//        dfs(nums, 0, n - 1, sum);
        return max;
    }

    int max = Integer.MIN_VALUE;
    int count = 0;

    public int dfs(int[] nums, int left, int right, int[][] sum) {
        if (left > right) {
            return 0;
        }
        if (sum[left][right] != Integer.MIN_VALUE) {
            return sum[left][right];
        }
        if (left == right) {
            return nums[left];
        }
        count++;
        sum[left][right] = nums[left] + dfs(nums, left + 1, right, sum);
        sum[left][right] = nums[right] + dfs(nums, left, right - 1, sum);
        max = Math.max(max, sum[left][right]);
        return sum[left][right];
    }

    public static void main(String[] args) {
        L_53 l53 = new L_53();
        System.out.println(l53.maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));
        System.out.println(l53.maxSubArray(new int[]{1}));
        System.out.println(l53.maxSubArray(new int[]{5, 4, -1, 7, 8}));
        System.out.println(l53.maxSubArray(new int[]{1, 2, -1, -2, 2, 1, -2, 1, 4, -5, 4}));
    }
}



