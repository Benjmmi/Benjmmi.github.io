package leetcode;

import java.util.HashSet;
import java.util.Set;

public class L_202 {
    public boolean isHappy(int n) {
        if (n == 1) {
            return true;
        }
        Set<Integer> integers = new HashSet<>();

        while (true) {
            if (integers.contains(n)) {
                return false;
            }
            integers.add(n);
            int result = 0;
            while (n > 0) {
                int mod = n % 10;
                result += mod*mod;
                n /= 10;
            }
            if (result == 1) {
                return true;
            }
            n = result;
        }
    }

    public static void main(String[] args) {
        L_202 l202 = new L_202();
        {
            System.out.println(l202.isHappy(2));
        }
        {
            System.out.println(l202.isHappy(19));
        }
        {
            System.out.println(l202.isHappy(7));
        }
    }
}
