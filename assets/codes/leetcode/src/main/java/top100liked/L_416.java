package top100liked;

import java.util.Arrays;

public class L_416 {
//    public boolean canPartition(int[] nums) {
//        int target = 0;
//        for (int num : nums) {
//            target += num;
//        }
//        if (target % 2 == 1) return false;
//        target /= 2;
//
//
//
//        return false;
//    }


    public boolean canPartition(int[] nums) {
        int target = 0;
        for (int num : nums) {
            target += num;
        }
        if (target % 2 == 1) return false;
        target /= 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;

        for (int num : nums) {
            for (int i = target; i >= num; i--) {
                dp[i] = dp[i] || dp[i - num];
            }
        }


        return dp[target];
    }

//    public boolean dfs(int[] nums, int target, int index) {
//        if (target == 0) return true;
//        if (target < 0 || index < 0) return false;
//        for (int i = index; i >= 0; i--) {
//            boolean flag = dfs(nums, target - nums[i], i - 1);
//            if (flag) return true;
//            for (; i - 1 >= 0 && nums[i] == nums[i - 1]; i--) {
//
//            }
//        }
//        return false;
//
//    }
//    public boolean dfs(int[] nums, int target, int index) {
//        if (target == 0) return true;
//        if (target < 0 || index < 0) return false;
//        for (int i = index; i >= 0; i--) {
//            boolean flag = dfs(nums, target - nums[i], i - 1);
//            if (flag) return true;
//            for (; i-1 >= 0 && nums[i] == nums[i - 1]; i--) {
//
//            }
//        }
//        return false;
//    }

    public static void main(String[] args) {
        L_416 l416 = new L_416();
//        System.out.println(l416.canPartition(new int[]{1, 3, 5}));
//        System.out.println(l416.canPartition(new int[]{1, 5, 11, 5}));
//        System.out.println(l416.canPartition(new int[]{1, 2, 5}));
//        System.out.println(l416.canPartition(new int[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 99, 97}));
        System.out.println(l416.canPartition(new int[]{4, 4, 4, 4, 4, 4, 4, 4, 8, 8, 8, 8, 8, 8, 8, 8, 12, 12, 12, 12, 12, 12, 12, 12, 16, 16, 16, 16, 16, 16, 16, 16, 20, 20, 20, 20, 20, 20, 20, 20, 24, 24, 24, 24, 24, 24, 24, 24, 28, 28, 28, 28, 28, 28, 28, 28, 32, 32, 32, 32, 32, 32, 32, 32, 36, 36, 36, 36, 36, 36, 36, 36, 40, 40, 40, 40, 40, 40, 40, 40, 44, 44, 44, 44, 44, 44, 44, 44, 48, 48, 48, 48, 48, 48, 48, 48, 52, 52, 52, 52, 52, 52, 52, 52, 56, 56, 56, 56, 56, 56, 56, 56, 60, 60, 60, 60, 60, 60, 60, 60, 64, 64, 64, 64, 64, 64, 64, 64, 68, 68, 68, 68, 68, 68, 68, 68, 72, 72, 72, 72, 72, 72, 72, 72, 76, 76, 76, 76, 76, 76, 76, 76, 80, 80, 80, 80, 80, 80, 80, 80, 84, 84, 84, 84, 84, 84, 84, 84, 88, 88, 88, 88, 88, 88, 88, 88, 92, 92, 92, 92, 92, 92, 92, 92, 96, 96, 96, 96, 96, 96, 96, 96, 97, 99}));
//        System.out.println(l416.canPartition(new int[]{3, 3, 6, 8, 16, 16, 16, 18, 20}));
    }

}
