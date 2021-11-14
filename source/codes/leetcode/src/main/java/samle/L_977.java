package samle;

import java.util.*;

public class L_977 {
    public static void main(String[] args) {

    }
    public int[] sortedSquares(int[] nums) {
        java.util.List<Integer> integerList = new java.util.ArrayList<Integer>();
        for (int i = 0; i < nums.length; i++) {
            integerList.add(nums[i]*nums[i]) ;
        }
        java.util.Collections.sort(integerList);
        int[] array = integerList.stream().mapToInt(i->i).toArray();
        return array;
    }
}
