package top100liked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class L_4 {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        List<Integer> list = new ArrayList<>();
        for (int i : nums2) {
            list.add(i);
        }
        for (int i : nums1) {
            list.add(i);
        }
        Collections.sort(list);
        if (list.size() % 2 == 0) {
            return (double) (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2;
        } else {
            return list.get(list.size() / 2);
        }
    }

    public static void main(String[] args) {

    }
}
