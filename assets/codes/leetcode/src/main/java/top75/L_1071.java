package top75;

public class L_1071 {
    public String gcdOfStrings(String str1, String str2) {
        if (str1.isEmpty() || str2.isEmpty()) {
            return str1 + str2;
        }
        if (str1.equals(str2)) {
            return str1;
        }
        if (str1.length() < str2.length()) {
            return gcdOfStrings(str2, str1);
        }
        if (str1.replaceAll(str2, "").isEmpty()) {
            return str2;
        }


        for (int i = str1.length(); i > 0; i--) {
            String sub = str1.substring(0, i);
            if (eq(sub, str1) && eq(sub, str2)) {
                return sub;
            }
        }
        return "";
    }


    public boolean eq(String sub, String s) {
        int i = s.length() / sub.length();
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < i; j++) {
            builder.append(sub);
        }
        return builder.toString().equals(s);
    }

    public static void main(String[] args) {
        L_1071 l1071 = new L_1071();
//        System.out.println(l1071.gcdOfStrings("ABABAB", "ABAB"));
        System.out.println(l1071.gcdOfStrings("LEET", "CODE"));
    }
}
