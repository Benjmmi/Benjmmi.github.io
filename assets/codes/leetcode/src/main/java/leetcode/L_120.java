package leetcode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class L_120 {
    public int minimumTotal(List<List<Integer>> triangle) {
        int row = triangle.size();
        for (int i = 0; i < row; i++) {
            if (i == 0) {
                continue;
            }
            int col = triangle.get(i).size();
            List<Integer> preRow = triangle.get(i - 1);
            for (int j = 0; j < col; j++) {
                int cur = preRow.get(Math.min(preRow.size() - 1, j));
                int pre = preRow.get(Math.max(0, j - 1));
                int num = triangle.get(i).get(j);
                int minSUm = num + Math.min(cur, pre);
                triangle.get(i).set(j, minSUm);
            }
        }
        List<Integer> integers = triangle.get(row - 1);
        int min = integers.get(0);
        for (Integer integer : integers) {
            if (integer < min) {
                min = integer;
            }
        }
        return min;
    }

    public static void main(String[] args) {
        L_120 l120 = new L_120();
        System.out.println(l120.minimumTotal(Arrays.asList(Arrays.asList(2), Arrays.asList(3, 4), Arrays.asList(6, 5, 7), Arrays.asList(4, 1, 8, 3))));
        System.out.println(l120.minimumTotal(Arrays.asList(Arrays.asList(-10))));
    }
}
