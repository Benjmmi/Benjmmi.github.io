package leetcode;

public class L_5 {
    int start = 0;
    int maxLength = 0;

    public String longestPalindrome(String s) {
        if (s.length() <= 1) {
            return s;
        }
        String max = "";
        for (int i = 0; i < s.length(); i++) {
            findPalindrome(s, i, i);
            findPalindrome(s, i, i + 1);
        }

        return s.substring(start, start + maxLength);
    }

    public void findPalindrome(String s, int left, int right) {
        int len = s.length();

        while (left >= 0 && right < len && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        int currentLength = right - left - 1;

        if (currentLength > maxLength) {
            maxLength = currentLength;
            start = left + 1;
        }
    }

    public static void main(String[] args) {
        L_5 l5 = new L_5();
        System.out.println(l5.longestPalindrome("babad"));
        System.out.println(l5.longestPalindrome("cbbd"));
    }
}
