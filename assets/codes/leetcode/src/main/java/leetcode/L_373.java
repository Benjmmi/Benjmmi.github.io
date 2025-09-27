package leetcode;

import java.util.*;
import java.util.stream.Collectors;

public class L_373 {

    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();

        PriorityQueue<int[]> lists = new PriorityQueue<>((o1, o2) -> o1[0] + o1[1] - o2[0] - o2[1]);

        for (int i = 0; i < nums1.length && i < k; i++) {
            lists.add(new int[]{nums1[i], nums2[0], 0});
        }

        while (k > 0 && !lists.isEmpty()) {
            int[] current = lists.poll();
            int num1 = current[0];
            int num2 = current[1];

            int num2Index = current[2];

            List<Integer> temp = Arrays.asList(num1, num2);
            result.add(temp);

            k--;
            if (num2Index + 1 < nums2.length) {
                lists.add(new int[]{num1, nums2[num2Index + 1], num2Index + 1});
            }
        }

        return result;
    }


    public static void main(String[] args) {
        L_373 l373 = new L_373();
        System.out.println(l373.kSmallestPairs(new int[]{1, 2, 3}, new int[]{1, 1, 2}, 2));
    }

}
