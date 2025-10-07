package leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class L_322 {

    int max;

    public int coinChange(int[] coins, int amount) {
        if (amount == 0) {
            return 0;
        }
        Arrays.sort(coins);
        if (coins[0] > amount) return -1;
        this.max = amount + 1;
        int[] answer = new int[amount + 1];
        Arrays.fill(answer, amount + 1);
        answer[0] = 0;
        for (int coin : coins) {
            if (coin > amount) {
                continue;
            }
            answer[coin] = 1;
        }
        for (int i = 1; i < amount + 1; i++) {
            for (int coin : coins) {
                if (i > coin) {
                    answer[i] = Math.min(answer[i], answer[i - coin] + 1);
                }
            }
        }
//        find(coins, amount, answer);
        return answer[amount] > amount ? -1 : answer[amount];
    }

    public int find(int[] coins, int amount, int[] answer) {
        if (amount <= coins[0]) return answer[amount];
        if (answer[amount] != max) return answer[amount];

        for (int length = coins.length - 1; length >= 0; length--) {
            if (coins[length] == amount) {
                answer[amount] = 1;
                break;
            }
            if (coins[length] > amount) {
                continue;
            }
            int value = find(coins, amount - coins[length], answer) + 1;
            answer[amount] = Math.min(answer[amount], value);
        }
        return answer[amount];
    }


    public static void main(String[] args) {
        L_322 l322 = new L_322();
//        System.out.println(l322.coinChange(new int[]{1, 2, 5}, 11));
//        System.out.println(l322.coinChange(new int[]{1, 3, 5}, 6));
//        System.out.println(l322.coinChange(new int[]{2, 5, 10, 1}, 27));
        System.out.println(l322.coinChange(new int[]{186, 419, 83, 408}, 6249));
        System.out.println(l322.coinChange(new int[]{1, 2147483647}, 2));
    }
}
