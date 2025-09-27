package leetcode;

import java.util.HashMap;
import java.util.Map;

public class L_136 {

    public int singleNumber(int[] nums) {
//        Map<Integer,Integer> map = new HashMap<>();
//        for (int num : nums) {
//            map.put(num,map.getOrDefault(num,0) + 1);
//        }
//        for (Map.Entry<Integer,Integer> entry : map.entrySet()) {
//            if (entry.getValue() == 1) {
//                return entry.getKey();
//            }
//        }
        int seral = 0;
        for (int num : nums) {
            seral ^= num;
        }
        return seral;
    }

}
