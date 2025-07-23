package leetcode;

import java.util.ArrayList;
import java.util.List;

public class L_151 {
    public String reverseWords(String s) {
        s = s.trim();
        String[] words = s.split(" ");
        int count = 0;
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                words[count] = words[i];
                count++;
            }
        }
        for (int i = 0; i < count / 2; i++) {
            String a = words[i];
            words[i] = words[count - 1 - i];
            words[count - 1 - i] = a;
        }
        for (int i = count; i < words.length; i++) {
            words[i] = "";
        }
        String result = String.join(" ", words);

        return result.trim();
    }

    public static void main(String[] args) {
        L_151 l151 = new L_151();
        {
            System.out.println(l151.reverseWords("the sky is blue"));
        }
        {
            System.out.println(l151.reverseWords("  hello world  "));
        }
        {
            System.out.println(l151.reverseWords("a good   example"));
        }
    }
}
