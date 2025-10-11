package top100liked;

import java.util.*;

public class L_560 {
    public int subarraySum(int[] nums, int k) {
        int count = 0;


        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);
        int preSum = 0;
        for (int i = 0; i < nums.length; i++) {
            preSum += nums[i];
            if (map.containsKey(preSum - k)) {
                count += map.get(preSum - k);
            }
            map.put(preSum, map.getOrDefault(preSum, 0) + 1);
        }


//        for (int i = 0; i < nums.length; i++) {
//            int sum = 0;
//            for (int j = i; j < nums.length; j++) {
//                sum += nums[j];
//                if (sum == k) {
//                    count++;
//                }
//            }
//        }


        return count;
    }

    public static void main(String[] args) {
        L_560 l560 = new L_560();
        System.out.println(l560.subarraySum(new int[]{1, 1, 1}, 2));
        System.out.println(l560.subarraySum(new int[]{1, 2, 3}, 3));
        System.out.println(l560.subarraySum(new int[]{1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 3}, 3));
        System.out.println(l560.subarraySum(new int[]{1}, 0));
        System.out.println(l560.subarraySum(new int[]{1}, 1));
    }
}
