package samle;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class L_1859 {
    public static void main(String[] args) {
        System.out.println(new L_1859().sortSentence("is2 sentence4 This1 a3"));;
    }
    public String sortSentence(String s) {
        String[] strings = s.split(" ");
        List<String> a = Arrays.asList(strings);
        Collections.sort(a, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.charAt(s.length()-1) - t1.charAt(t1.length()-1);
            }
        });
        StringBuilder builder = new StringBuilder();
        for (String s1 : a) {
            builder.append(s1.substring(0,s1.length()-1)).append(" ");
        }
        return builder.toString().trim();
    }
}
