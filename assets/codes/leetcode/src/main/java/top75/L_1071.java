package top75;

public class L_1071 {
    public String gcdOfStrings(String str1, String str2) {
        if (str1.isEmpty() || str2.isEmpty()) {
            return str1 + str2;
        }
        if (str1.length() < str2.length()) {
            return gcdOfStrings(str2, str1);
        }
        int len1 = str1.length();
        int len2 = str2.length();

        String common = str2;

        for (int i = 0; i < str2.length(); i++) {
            String sub = str2.substring(i);
            String s = str1.replaceAll(sub, "");
            if (s.isEmpty() && sub.length() < common.length()) {
                common = sub;
            }
        }
        return common;
    }

    public static void main(String[] args) {

    }
}
