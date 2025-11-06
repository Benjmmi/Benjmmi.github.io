package contest;

public class L_Q1 {
    public long removeZeros(long n) {
        int i = 1;
        long result = 0;
        while (n > 0) {
            long val = n % 10;
            long cur = n / 10;
            n /= 10;
            if (val == 0) {
                continue;
            }
            result = val * i + result;
            i *= 10;
        }
        return result;
    }

}
