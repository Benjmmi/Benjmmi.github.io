package top100liked;

public class L_198 {
    public int rob(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }
        if (nums.length == 2) {
            return Math.max(nums[0], nums[1]);
        }

        nums[1] = Math.max(nums[0], nums[1]);
        for (int i = 2; i < nums.length; i++) {
            nums[i] = Math.max(nums[i-1], nums[i-2]+nums[i]);
        }

        return nums[nums.length-1];

//
//        int[] dp = new int[nums.length];
//        dp[0] = nums[0];
//        dp[1] = Math.max(nums[0], nums[1]);
//
//        for (int i = 2; i < nums.length; i++) {
//            dp[i] = Math.max(dp[i - 2] + nums[i], dp[i - 1]);
//        }
//
//        return dp[nums.length - 1];
    }

    public static void main(String[] args) {
        L_198 l198 = new L_198();
        System.out.println(l198.rob(new int[]{1,2,3,1}));
        System.out.println(l198.rob(new int[]{2,7,9,3,1}));
    }
}
