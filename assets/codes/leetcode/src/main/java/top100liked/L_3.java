package top100liked;

import java.util.Arrays;

public class L_3 {
    public int lengthOfLongestSubstring(String s) {
        if (s.isEmpty()) {
            return 0;
        }
        int maxLength = 0;
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < s.length(); i++) {
//            char c = s.charAt(i);
//            int index = builder.indexOf(String.valueOf(c));
//            if (index != -1) {
//                builder = new StringBuilder(builder.substring(index+1));
//            }
//            builder.append(c);
//            maxLength = Math.max(maxLength, builder.length());
//            if (maxLength == 128) {
//                break;
//            }
//        }
        int[] alphabet = new int[128];
        Arrays.fill(alphabet, -1);
        int ignore = 0;
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int index = s.charAt(i);
            length++;
            if (alphabet[index] == -1 || alphabet[index] < ignore) {
                alphabet[index] = i;
                maxLength = Math.max(maxLength, length);
                maxLength = Math.max(maxLength, i - ignore);
                continue;
            }
            length = 0;
            int val = i - alphabet[index];
            ignore = alphabet[index];
            maxLength = Math.max(maxLength, val);
            alphabet[index] = i;
            if (maxLength == 128){
                break;
            }
        }
        return maxLength;
    }

    public static void main(String[] args) {
        L_3 l = new L_3();
        System.out.println(l.lengthOfLongestSubstring("abcabcbb"));
        System.out.println(l.lengthOfLongestSubstring("pwwkew"));
    }
}
