package top100liked;

import java.util.Arrays;

public class L_322 {
    public int coinChange(int[] coins, int amount) {
        if (amount == 0) return 0;
        Arrays.sort(coins);
        int[] dp = new int[amount + 1];

        Arrays.fill(dp, Integer.MAX_VALUE);

        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int j = 0; j < coins.length && coins[j] <= i; j++) {
                if (dp[i - coins[j]] != Integer.MAX_VALUE) {
                    dp[i] = Math.min(dp[i], dp[i - coins[j]] + 1);
                }
            }
        }


        return dp[amount] > amount ? -1 : dp[amount];
    }


//    int max;
//
//    public int coinChange(int[] coins, int amount) {
//        if (amount == 0){
//            return 0;
//        }
//        Arrays.sort(coins);
//        if (coins[0] > amount) return -1;
//        this.max = amount + 1;
//        int[] answer = new int[amount + 1];
//        Arrays.fill(answer, amount + 1);
//        answer[0] = 0;
//        for (int coin : coins) {
//            if (coin > amount) {
//                continue;
//            }
//            answer[coin] = 1;
//        }
//        for (int i = 1; i < amount+1; i++) {
//            for (int coin : coins) {
//                if (i>coin){
//                    answer[i] = Math.min(answer[i], answer[i-coin] + 1);
//                }
//            }
//        }

    /// /        find(coins, amount, answer);
//        return answer[amount] > amount ? -1 : answer[amount];
//    }
//
//    public int find(int[] coins, int amount, int[] answer) {
//        if (amount <= coins[0]) return answer[amount];
//        if (answer[amount] != max) return answer[amount];
//
//        for (int length = coins.length - 1; length >= 0; length--) {
//            if (coins[length] == amount) {
//                answer[amount] = 1;
//                break;
//            }
//            if (coins[length] > amount) {
//                continue;
//            }
//            int value = find(coins, amount - coins[length], answer) + 1;
//            answer[amount] = Math.min(answer[amount], value);
//        }
//        return answer[amount];
//    }
    public static void main(String[] args) {
        L_322 l322 = new L_322();
//        System.out.println(l322.coinChange(new int[]{1, 2, 5}, 3));
        System.out.println(l322.coinChange(new int[]{2}, 3));
    }
}
