package top100liked;

public class L_121 {

    public int maxProfit(int[] prices) {
        int max = 0;
        int index = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] < prices[index]) {
                index = i;
                continue;
            }
            max = Math.max(max, prices[i] - prices[index]);
        }
        return max;
    }
}
