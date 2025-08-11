package leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class L_1 {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> l = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int sub = target - nums[i];
            if (l.containsKey(sub)) {
                return new int[]{l.get(sub), i};
            }
            l.put(nums[i], i);
        }
        return new int[]{};
    }

    public static void main(String[] args) {
        L_1 l1 = new L_1();
        {
            System.out.println(l1.twoSum(new int[]{2, 7, 11, 15}, 15));
        }
        {
            System.out.println(l1.twoSum(new int[]{3, 2, 4}, 6));
        }
        {
            System.out.println(l1.twoSum(new int[]{3, 3}, 6));
        }
    }
}
