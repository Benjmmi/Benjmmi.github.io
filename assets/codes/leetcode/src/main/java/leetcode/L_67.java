package leetcode;

public class L_67 {
    char[] chars = new char[]{'0', '1'};

    public String addBinary(String a, String b) {
        if (b.length() > a.length()) {
            return addBinary(b, a);
        }
        char[] aC = a.toCharArray();
        char[] bC = b.toCharArray();
        char[] result = new char[a.length()];
        int carry = 0;
        int j = aC.length - 1;
        for (int i = bC.length - 1; i >= 0; i--, j--) {
            int ans = (aC[j] - '0') + (bC[i] - '0') + carry;
            carry = ans / 2;
            result[j] = chars[ans % 2];
        }

        while (j >= 0) {
            int ans = aC[j] - '0' + carry;
            carry = ans / 2;
            result[j] = chars[ans % 2];
            j--;
        }
        return (carry > 0 ? "1" : "")+new String(result);
    }

    public static void main(String[] args) {
        L_67 l = new L_67();
        System.out.println(l.addBinary("11", "1"));
        System.out.println(l.addBinary("1010", "1011"));
    }
}
