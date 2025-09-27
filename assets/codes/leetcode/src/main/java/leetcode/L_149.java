package leetcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class L_149 {
    public int maxPoints(int[][] points) {
        int n = points.length;
        int ans = 0;
        for (int i = 0; i < n - 1; i++) {
            int[] p = points[i];
            Map<Double, Integer> map = new HashMap<>();
            for (int j = i + 1; j < n; j++) {
                int[] q = points[j];
                int dx = q[0] - p[0];
                int dy = q[1] - p[1];
                double k = dx != 0 ? (double) dy / dx : Double.POSITIVE_INFINITY;
                if (k == 0) {
                    k = 0;
                }
                int c = map.merge(k, 1, Integer::sum);
                ans = Math.max(ans, c);
            }
        }
        return ans + 1;
    }

    public int maxPoints2(int[][] points) {

        if (points.length <= 1) return points.length;
        Map<Double, Set<String>> doubleIntegerMap = new HashMap<>();
        int max = 0;
        int n = points.length;
        for (int i = 0; i < n; i++) {
            int[] p = points[i];


            for (int j = i + 1; j < n; j++) {
                int[] q = points[j];
                int x = p[0] - q[0];
                int y = p[1] - q[1];
                double kl = x != 0 ? (double) y / x : Double.POSITIVE_INFINITY;
                if (kl == 0) {
                    kl = 0;
                }
                Set<String> l = doubleIntegerMap.computeIfAbsent(kl, k -> new HashSet<>());
                l.add(p[0] + "_" + p[1]);
                l.add(q[0] + "_" + q[1]);
                max = Math.max(max, doubleIntegerMap.get(kl).size());
            }
        }
        return max;
    }

    public static void main(String[] args) {
        L_149 l149 = new L_149();
//        System.out.println(l149.maxPoints(new int[][]{{0, 1}, {0, 0}}));
        System.out.println(l149.maxPoints(new int[][]{{1, 1}, {3, 2}, {5, 3}, {4, 1}, {2, 3}, {1, 4}}));
//        System.out.println(l149.maxPoints2(new int[][]{{1, 1}, {3, 2}, {5, 3}, {4, 1}, {2, 3}, {1, 4}}));
    }
}
