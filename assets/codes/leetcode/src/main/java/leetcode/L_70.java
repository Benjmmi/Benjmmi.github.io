package leetcode;

public class L_70 {
    public int climbStairs(int n) {
        if(n<=2){
            return n;
        }
//        if(map.get(n) != null){
//            return map.get(n);
//        }
//        int step = climbStairs(n-1)+climbStairs(n-2);
//        map.put(n,step);
//        return step;

        int[] dp = new int[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        dp[2] = 2;
        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }
}
