package top100liked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L_279 {
    public int numSquares(int n) {
        int[] dp = new int[n + 1];
        dp[0] = 0;


        for (int i = 1; i <= n; i++) {
            dp[i] = i;
            for (int j = 1; j*j <= i; j++) {
                int square = j * j;
                dp[i] = Math.min(dp[i], 1 + dp[i - square]);
            }
        }
        return dp[n];
    }
//    public int numSquares(int n) {
//        min = Integer.MAX_VALUE;
//        if (n <= 2) return n;
//        List<Integer> pow = new ArrayList<>();
//        for (int j = 1; j <= n / 2 && j * j < n; j++) {
//            pow.add(j * j);
//        }
//        canJump(n, new HashMap<>(), pow, n);
//        return min;
//    }
//
//    int min = Integer.MAX_VALUE;
//
//    public int canJump(int n, Map<Integer, Integer> map, List<Integer> pow, int skip) {
//        if (skip == 0) {
//            return 0;
//        }
//        if (skip < 0) {
//            return -1;
//        }
//        if (map.containsKey(n)) {
//            return map.get(n);
//        }
//        int size = pow.size() - 1;
//        for (int i = size; i >= 0; i--) {
//            if (pow.get(i) > n) {
//                continue;
//            }
//            int step = canJump(n, map, pow, skip - pow.get(i));
//            if (step == -1) {
//                map.put(skip - pow.get(i), step);
//                continue;
//            }
//            map.put(n, step + 1);
//            if (skip == n) {
//                min = Math.min(min, step + 1);
//            }
//        }
//        return -1;
//    }

    public static void main(String[] args) {
        L_279 l279 = new L_279();
        System.out.println(l279.numSquares(12));
        System.out.println(l279.numSquares(88));
        System.out.println(l279.numSquares(13));
        System.out.println(l279.numSquares(3));


    }
}
