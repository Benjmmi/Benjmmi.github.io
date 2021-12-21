package nowcoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

public class BL13 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] s = br.readLine().split(" ");
        int n = Integer.parseInt(s[0]);
        long k = Long.parseLong(s[1]);
        long[] num = new long[n];
        String[] s1 = br.readLine().split(" ");
        for (int i = 0; i < n; i++) {
            num[i] = Long.parseLong(s1[i]);
        }

        Arrays.sort(num);
        // 0 1 2 3 4
        // 1 3 4 5 9

        long min = 0, max = num[n - 1] * num[n - 2];
        while (min < max) {
            long mid = (min + max + 1) / 2; // 中位数   // 28
            int left = 0, right = n - 1; // 左右坐标
            long cur = 0; // 当前值
            // 左坐标小于右坐标，当前值小目标值
            while (left < right && cur < k) { // 0 -> 4
                // 左坐标小于右坐标，左坐标值*右坐标值小于中间值
                while (left < right && num[left] * num[right] < mid) {
                    left++; // 2
                }
                cur += right - left > 0 ? right - left : 0; //14
                right--; // 3
            }
            if (cur > k) { //
                min = mid; // 14
            } else {
                max = mid - 1;
            }
        }
        System.out.println(min);
    }
}
