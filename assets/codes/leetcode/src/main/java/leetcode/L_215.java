package leetcode;

import java.util.Comparator;
import java.util.PriorityQueue;

public class L_215 {


    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> integers = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2-o1;
            }
        });
        for (int num : nums) {
            integers.offer(num);
        }
        for (int i = 0; i < k-1; i++) {
            integers.poll();
        }
        return integers.poll();
    }

    public static void main(String[] args) {
        L_215 l215 = new L_215();
        System.out.println(l215.findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2));
    }
}
