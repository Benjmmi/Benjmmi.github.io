package leetcode;

public class L_9 {
    public boolean isPalindrome(int x) {

        if (x < 0) return false;

        String s = String.valueOf(x);
        char[] cs = s.toCharArray();

        int j = cs.length - 1;
        for (int i = 0; i < cs.length/2; i++,j--) {
            if (cs[i] != cs[j]) return false;
        }

        return true;
    }

    public static void main(String[] args) {

    }
}
