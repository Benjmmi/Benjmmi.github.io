package leetcode;

import java.util.HashMap;
import java.util.Map;

public class L_219 {
    public boolean containsNearbyDuplicate(int[] nums, int k) {

        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (!map.containsKey(nums[i])) {
                map.put(nums[i], i);
                continue;
            }
            Integer value = map.get(nums[i]);
            int result = Math.abs(value - i);
            map.put(nums[i], i);
            if (result <= k) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        L_219 l219 = new L_219();
        {
            System.out.println(l219.containsNearbyDuplicate(new int[]{1, 2, 3, 1}, 3));
        }
        {
            System.out.println(l219.containsNearbyDuplicate(new int[]{1, 0, 1, 1}, 1));
        }
        {
            System.out.println(l219.containsNearbyDuplicate(new int[]{1, 2, 3, 1, 2, 3}, 2));
        }
    }
}
