package leetcode;

import java.util.ArrayList;
import java.util.List;

public class L_66 {
    public int[] plusOne(int[] digits) {
        digits[digits.length - 1] += 1;
        int cross = digits[digits.length - 1] / 10;
        digits[digits.length - 1] = digits[digits.length - 1] % 10;
        for (int i = digits.length - 2; i >= 0; i--) {
            if (cross == 0){
                break;
            }
            int temp = digits[i] + cross;
            digits[i] = temp % 10;
            cross = temp / 10;
        }

        if (cross == 0){
            return digits;
        }
        int[] res = new int[digits.length + 1];
        res[0] = cross;
        for (int i = 0; i < digits.length; i++) {
            res[i + 1] = digits[i];
        }
        return res;
    }

    public static void main(String[] args) {

    }
}
