package top100liked;

public class L_5 {
    int start = 0;
    int maxLength = 0;

    public String longestPalindrome(String s) {
        if (s.length() <= 1) return s;
        for (int i = 0; i < s.length(); i++) {
            isPalindrome(s, i, i);
            isPalindrome(s, i, i + 1);
        }
        return s.substring(start, start + maxLength);
    }


    public void isPalindrome(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        if (right - left - 1 > maxLength) {
            maxLength = right - left - 1;
            start = left + 1;
        }
    }


    public static void main(String[] args) {

    }
}
