package top100liked;

import java.util.*;

public class L_215 {

    public int findKthLargest(int[] nums, int k) {
        Queue<Integer> queue = new PriorityQueue<>();
        for (int num : nums) {
            queue.add(num);
            while (!queue.isEmpty() && queue.size() > k) {
                queue.poll();
            }
        }
        return queue.poll();
    }

    public static void main(String[] args) {
        L_215 l215 = new L_215();
        System.out.println(l215.findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2));
        System.out.println(l215.findKthLargest(new int[]{3,2,3,1,2,4,5,5,6}, 2));
    }
}
