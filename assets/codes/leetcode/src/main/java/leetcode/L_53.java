package leetcode;

public class L_53 {
    public int maxSubArray(int[] nums) {
        int currentSum = 0;
        int globalMaxSum = Integer.MIN_VALUE;
        for (int num : nums) {
            if (currentSum + num < num) {
                currentSum = num;
            } else {
                currentSum += num;
            }
            globalMaxSum = Math.max(currentSum, globalMaxSum);
        }
        return globalMaxSum;
    }

    public static void main(String[] args) {
        L_53 l53 = new L_53();
        System.out.println(l53.maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));
        System.out.println(l53.maxSubArray(new int[]{1}));
        System.out.println(l53.maxSubArray(new int[]{5, 4, -1, 7, 8}));
    }
}
