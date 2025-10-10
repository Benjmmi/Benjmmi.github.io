package leetcode;

public class L_123 {
    public int maxProfit(int[] prices) {

        int max = 0;
        int mark = 0;
        int[] leftProfit = new int[prices.length];
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] - prices[mark] < 0) {
                mark = i;
            } else {
                max = Math.max(max, prices[i] - prices[mark]);
            }
            leftProfit[i] = max;
        }

        int[] rightProfit = new int[prices.length];
        mark = prices.length - 1;
        max = 0;
        for (int i = prices.length - 1; i >= 0; i--) {
            int val = -(prices[i] - prices[mark]);
            if (val < 0) {
                mark = i;
            } else {
                max = Math.max(max, val);
            }
            rightProfit[i] = max;
        }

        max = 0;
        for (int i = 0; i < prices.length; i++) {
            max = Math.max(max, leftProfit[i] + rightProfit[i]);
        }
//        for (int i = 0; i < prices.length; i++) {
//            for (int j = i + 1; j < prices.length; j++) {
//                dp[i][j] = Math.max(prices[j] - prices[i], 0);
////                int start = j + 1;
////                sumMax = Math.max(dp[i][j], sumMax);
////                for (int k = start; k < prices.length; k++) {
////                    sumMax = Math.max(sumMax, dp[i][j] + Math.max(prices[k] - prices[start], 0));
////                }
//            }
//        }

//        for (int i = 0; i < prices.length; i++) {
//            for (int j = i + 1; j < prices.length; j++) {
//                int val1 = dp[i][j];
//                sumMax = Math.max(val1, sumMax);
//                for (int k = j + 1; k < prices.length; k++) {
//                    for (int l = k+1; l < prices.length; l++) {
//                        sumMax = Math.max(sumMax, val1+dp[k][l]);
//                    }
//                }
//            }
//        }

        return max;
    }


    public static void main(String[] args) {
        L_123 l123 = new L_123();
        System.out.println(l123.maxProfit(new int[]{3, 2, 6, 5, 0, 3})); // 6
//        System.out.println(l123.maxProfit(new int[]{3, 3, 5, 0, 0, 3, 1, 4}));
//        System.out.println(l123.maxProfit(new int[]{1, 2, 3, 4, 5}));
//        System.out.println(l123.maxProfit(new int[]{7, 6, 4, 3, 1}));
//        System.out.println(l123.maxProfit(new int[]{7, 6, 4, 3, 1}));
    }
}
