package leetcode;

import java.math.BigDecimal;

public class L_172 {
    public int trailingZeroes(int n) {
        // 10
        int num = 0;

        for (int i = 5; i <= n; i += 5) {
            for (int x = i; x % 5 == 0; x /= 5) {
                num++;
            }
        }

        return num;
    }

    public static void main(String[] args) {
        L_172 l172 = new L_172();

        // 5  -- 1
//        System.out.println("5 - " + l172.trailingZeroes(5));
//        // 10 -- 2
//        System.out.println("10 - " + l172.trailingZeroes(10));
//        // 15 -- 3
//        System.out.println("15 - " + l172.trailingZeroes(15));
//        // 20 -- 4
//        System.out.println("20 - " + l172.trailingZeroes(20));
//        // 25 -- 6
//        System.out.println("25 - " + l172.trailingZeroes(25));
//        // 30 -- 7
//        System.out.println("30 - " + l172.trailingZeroes(30));
//        // 35 -- 8
//        System.out.println("35 - " + l172.trailingZeroes(35));
//        // 40 -- 9
//        System.out.println("40 - " + l172.trailingZeroes(40));
//        // 45 -- 11
//        System.out.println("45 - " + l172.trailingZeroes(45));
        // 125 -- 31
        System.out.println("125 - " + l172.trailingZeroes(125));
        // 625 -- 156
        System.out.println("625 - " + l172.trailingZeroes(625));
    }
}
