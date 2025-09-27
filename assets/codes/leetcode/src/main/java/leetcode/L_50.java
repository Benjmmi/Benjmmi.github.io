package leetcode;

public class L_50 {
    public double myPow(double x, int n) {
        if (x == 0) return 0;
        if (n == 0) return 1;
        if (x == 1) return 1;
        if (x == -1) return n%2==0?1:-1;
        long p = n;
        if (n < 0){
            x = 1.0 / x;
            p = -p;
        }
        double val = 1;
        for (long i = 0; i < p; i++) {
            val = val * x;
        }
        return val;
    }

    public static void main(String[] args) {
        L_50 l50 = new L_50();
//        System.out.println(l50.myPow(2.0, -2));
//        System.out.println(l50.myPow(2.0, -2147483648));
        System.out.println(l50.myPow(-2.0, 2));
    }
}
