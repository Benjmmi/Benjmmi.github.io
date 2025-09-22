package leetcode;

import java.util.ArrayDeque;
import java.util.Queue;

public class L_918 {
    public int maxSubarraySumCircular(int[] nums) {
        int currentSumMax = 0;
        int globalSUmMax = Integer.MIN_VALUE;
        int currentSumMin = 0;
        int globalSUmMin = 0;

        int sum = 0;

        for (int num : nums) {
            currentSumMax = Math.max(currentSumMax, 0) + num;
            globalSUmMax = Math.max(currentSumMax, globalSUmMax);

            currentSumMin = Math.min(currentSumMin, 0) + num;
            globalSUmMin = Math.min(currentSumMin, globalSUmMin);

            sum += num;
        }


        return globalSUmMin == sum ? globalSUmMax : Math.max(globalSUmMax, sum - globalSUmMin);
    }

    public static void main(String[] args) {
        L_918 l918 = new L_918();
        System.out.println(l918.maxSubarraySumCircular(new int[]{5, -3, 5}));
        System.out.println(l918.maxSubarraySumCircular(new int[]{-1, 3, -3, 9, -6, 8, -5, -5, -6, 10}));
        System.out.println(l918.maxSubarraySumCircular(new int[]{-9, 14, 24, -14, 12, 18, -18, -10, -10, -23, -2, -23, 11, 12, 18, -9, 9, -29, 12, 4, -8, 15, 11, -12, -16, -9, 19, -12, 22, 16}));
    }
}
