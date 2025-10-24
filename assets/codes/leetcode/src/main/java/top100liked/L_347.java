package top100liked;

import java.util.*;

public class L_347 {

    public int[] topKFrequent(int[] nums, int k) {
        Queue<int[]> queue = new PriorityQueue<>((o1, o2) -> o2[1] - o1[1]);
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> item : map.entrySet()) {
            queue.offer(new int[]{item.getKey(), item.getValue()});
        }

        int[] res = new int[k];
        int i = 0;
        while (!queue.isEmpty() && i < k) {
            res[i] = queue.poll()[0];
            i++;
        }
        return res;
    }

    public static void main(String[] args) {
        L_347 l = new L_347();
        System.out.println(Arrays.toString(l.topKFrequent(new int[]{4, 1, -1, 2, -1, 2, 3}, 2)));
//        System.out.println(Arrays.toString(l.topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2)));
    }
}
