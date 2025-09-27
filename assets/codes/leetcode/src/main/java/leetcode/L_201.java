package leetcode;

public class L_201 {
    public int rangeBitwiseAnd(int left, int right) {
        long ll = left;
        long rr = right;
        if ((left & right) == 0) {
            return 0;
        }

        long start = 1;
        while (start << 1 < right) {
            start <<= 1;
        }

        if (start == ll){
            return left;
        }


        for (long i = Math.max(ll,start); i <= rr; i++) {
            ll = ll & i;
            if (ll == 0) {
                return 0;
            }
        }
        return (int) ll;
    }

    public static void main(String[] args) {
        L_201 l201 = new L_201();
//        System.out.println(l201.rangeBitwiseAnd(5, 7));
//        System.out.println(l201.rangeBitwiseAnd(0, 0));
//        System.out.println(l201.rangeBitwiseAnd(1, 2147483647));
//        System.out.println(l201.rangeBitwiseAnd(2147483646, 2147483647));
        System.out.println(l201.rangeBitwiseAnd(1073741824, 2147483647));
    }
}
