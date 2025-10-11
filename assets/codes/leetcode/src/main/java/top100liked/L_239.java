package top100liked;

import java.util.*;

public class L_239 {
    public int[] maxSlidingWindow(int[] nums, int k) {
        PriorityQueue<int[]> pq = new PriorityQueue<>(k, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] != o2[0] ? o2[0] - o1[0] : o2[1] - o1[1];
            }
        });
        for (int i = 0; i < k; i++) {
            pq.offer(new int[]{nums[i], i});
        }
        int[] res = new int[nums.length - k + 1];
        res[0] = pq.peek()[0];
        for (int i = k; i < nums.length; i++) {
            pq.offer(new int[]{nums[i], i});
            while (pq.peek()[1] <= i - k) {
                pq.poll();
            }
            res[i - k + 1] = pq.peek()[0];
        }

        return res;
    }

    public static void main(String[] args) {
        L_239 l239 = new L_239();
//        println(l239.maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3));
        println(l239.maxSlidingWindow(new int[]{9, 10, 9, -7, -4, -8, 2, -6}, 5));
    }

    private static void println(int[] ints) {
        for (int anInt : ints) {
            System.out.print(anInt + ",");
        }
        System.out.println();
    }

}
