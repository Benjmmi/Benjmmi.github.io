package nowcoder;

public class ME1 {

    public static void main(String[] args) {
        int[] a = new int[]{1, 2, 3, 4, 5};
        int[] b = new int[]{1, 2, 3, 4, 5};
        int k = 3;
        int n = 5;
        k = n * n - k + 1;
        System.out.println(k);
        System.out.println(solve(n, a, b, k));
    }

    public static boolean check(int val, int n, int[] a, int[] b, int k) {
        int cnt = 0;
        for (int i = 0, j = n - 1; i < n; ++i) {
            while (j >= 0 && a[i] * b[j] > val) {
                --j;
            }
            cnt += j + 1;
        }
        return cnt >= k;
    }

    public static int solve(int n, int[] a, int[] b, int k) {
        int l = a[0] * b[0], r = a[n - 1] * b[n - 1];
        while (l < r) {
            int mid = (l + r) >> 1;
            if (!check(mid, n, a, b, k)) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }
        return l;
    }

}
