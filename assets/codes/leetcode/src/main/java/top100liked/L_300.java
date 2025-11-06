package top100liked;

import java.util.*;

public class L_300 {

    public int lengthOfLIS(int[] nums) {
        int max = 0;

        Map<Integer, Integer> map = new TreeMap<>();

        int[] dp = new int[nums.length + 1];

        for (int i = 0; i < nums.length; i++) {
            dp[i] = 1;
            for (Map.Entry<Integer, Integer> item : map.entrySet()) {
                if (item.getKey() < nums[i]) {
                    dp[i] = Math.max(dp[i], map.get(item.getKey()) + 1);
                } else {
                    break;
                }
            }
            if (map.containsKey(nums[i])) {
                map.put(nums[i], Math.max(map.get(nums[i]), dp[i]));
            } else {
                map.put(nums[i], dp[i]);
            }
            max = Math.max(max, dp[i]);
        }

        return max;
    }


//    public int lengthOfLIS(int[] nums) {
//        // 比他小的数字
//        Set<Integer> set = new TreeSet<>();
//        // 排序数组
//        int[] sorts = new int[nums.length];
//        Arrays.fill(sorts, 1);
//        // 数字序列
//        Map<Integer, List<Integer>> map = new HashMap<>();
//        int max = 1;
//        for (int i = 0; i < nums.length; i++) {
//            for (Integer item : set) {
//                if (nums[i] <= item) {
//                    break;
//                }
//                List<Integer> list = map.getOrDefault(item, new ArrayList<>());
//                for (Integer index : list) {
//                    if (index < i) {
//                        sorts[i] = Math.max(sorts[i], sorts[index] + 1);
//                        max = Math.max(max, sorts[i]);
//                    }
//                }
//                map.put(item, list);
//            }
//
//            //
//            set.add(nums[i]);
//            if (map.get(nums[i]) == null) {
//                List<Integer> l = new ArrayList<>();
//                l.add(i);
//                map.put(nums[i], l);
//            } else {
//                map.get(nums[i]).add(i);
//            }
//        }
//        return max;
//    }


    public static void main(String[] args) {
        L_300 l300 = new L_300();
        System.out.println(l300.lengthOfLIS(new int[]{10, 9, 2, 5, 3, 7, 101, 18}));
        System.out.println(l300.lengthOfLIS(new int[]{0, 1, 0, 3, 2, 3}));
        System.out.println(l300.lengthOfLIS(new int[]{7, 7, 7, 7, 7, 7, 7}));
        System.out.println(l300.lengthOfLIS(new int[]{3, 5, 6, 2, 5, 4, 19, 5, 6, 7, 12}));
    }
}
