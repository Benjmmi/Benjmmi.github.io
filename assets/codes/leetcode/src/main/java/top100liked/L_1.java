package top100liked;

import java.util.HashMap;
import java.util.Map;

public class L_1 {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer,Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            Integer val = map.get(target - nums[i]);
            if (val != null) {
                return new int[]{val,i};
            }
            map.put(nums[i],i);
        }
        return null;
    }

    public static void main(String[] args) {

    }
}
