package top75;

public class L_1768 {
    public String mergeAlternately(String word1, String word2) {
        StringBuilder sb = new StringBuilder();
        int len1 = word1.length();
        int len2 = word2.length();
        if (len1 == 0 || len2 == 0) {
            return word1 + word2;
        }
        int a = 0;
        int b = 0;
        for (int i = 0; i < len1 + len2; i++) {
            if (a <= b && a < len1 || b >= len2) {
                sb.append(word1.charAt(a));
                a++;
            } else {
                sb.append(word2.charAt(b));
                b++;
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {

    }
}
