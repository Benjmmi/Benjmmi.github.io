package interview_100;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class L_624 {

    public int maxDistance(List<List<Integer>> arrays) {
        if (arrays == null || arrays.size() <= 1) {
            return 0;
        }
//        int res = 0;
//        int min = arrays.get(0).get(0);
//        int max = arrays.get(0).get(arrays.get(0).size() - 1);
//
//        for (int i = 1; i < arrays.size(); i++) {
//            List<Integer> array = arrays.get(i);
//            int n = array.size();
//            res = Math.max(res, Math.max(Math.abs(array.get(n - 1) - min), Math.abs(array.get(0) - max)));
//            min = Math.min(min, array.get(0));
//            max = Math.max(max, array.get(n - 1));
//        }
//        return res;


        int[][] mins = new int[arrays.size()][2];
        int[][] maxs = new int[arrays.size()][2];
        for (int i = 0; i < arrays.size(); i++) {
            mins[i][0] = arrays.get(i).get(0);
            mins[i][1] = i;
            maxs[i][0] = arrays.get(i).get(arrays.get(i).size() - 1);
            maxs[i][1] = i;
        }
        Arrays.sort(mins, (o1, o2) -> o1[0] - o2[0]);
        Arrays.sort(maxs, (o1, o2) -> o2[0] - o1[0]);

        if (maxs[0][1] == mins[0][1]) {
            return Math.max(Math.abs(maxs[0][0] - mins[1][0]),Math.abs(maxs[1][0] - mins[0][0]));
        } else {
            return Math.abs(maxs[0][0] - mins[0][0]);
        }
    }

    public static void main(String[] args) {
        L_624 l624 = new L_624();

        System.out.println(l624.maxDistance(Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5), Arrays.asList(1, 2, 3))));
    }
}
