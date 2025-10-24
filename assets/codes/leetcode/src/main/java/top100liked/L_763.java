package top100liked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class L_763 {
    public List<Integer> partitionLabels(String s) {
        List<Integer> res = new ArrayList<>();

        char[] chars = s.toCharArray();
        int[] point = new int[26];
        for (int i = 0; i < chars.length; i++) {
            int c = chars[i] - 'a';
            point[c] = Math.max(point[c], i);
        }

        int start = 0;
        int end = point[chars[0] - 'a'];
        for (int i = 0; i < chars.length; i++) {
            int c = chars[i] - 'a';
            if (point[c] > end) {
                end = point[c];
            }
            if (i == end) {
                res.add(end - start + 1);
                start = i + 1;
            }

        }

//        int[] minPoint = new int[26];
//        int[] maxPoint = new int[26];
//        int len = s.length();
//
//        Arrays.fill(minPoint, Integer.MAX_VALUE);
//        Arrays.fill(maxPoint, Integer.MIN_VALUE);
//
//        for (int i = 0; i < s.length(); i++) {
//            int index = s.charAt(i) - 'a';
//            minPoint[index] = Math.min(minPoint[index], i);
//            maxPoint[index] = Math.max(maxPoint[index], i);
//        }
//
//        int minIndex = minPoint[s.charAt(0) - 'a'];
//        int maxIndex = maxPoint[s.charAt(0) - 'a'];
//
//        while (true) {
//            for (int i = minIndex; i < maxIndex; i++) {
//                int index = s.charAt(i) - 'a';
//                if ((maxPoint[index] <= maxIndex && maxPoint[index] >= minIndex)
//                || (minPoint[index] <= maxIndex && minPoint[index] >= minIndex)) {
//                    maxIndex = Math.max(maxIndex, maxPoint[index]);
//                }
//            }
//            res.add(maxIndex - minIndex + 1);
//            if (maxIndex + 1 >= s.length()) {
//                break;
//            }
//            char c = s.charAt(maxIndex + 1);
//            maxIndex = maxPoint[c - 'a'];
//            minIndex = minPoint[c - 'a'];
//        }


        return res;


    }

    public static void main(String[] args) {
        L_763 l763 = new L_763();
//        System.out.println(l763.partitionLabels("ababcbacadefegdehijhklij"));
//        System.out.println(l763.partitionLabels("eccbbbbdec"));
        System.out.println(l763.partitionLabels("ntswuqqbidunnixxpoxxuuupotaatwdainsotwvpxpsdvdbwvbtdiptwtxnnbtqbdvnbowqitudutpsxsbbsvtipibqpvpnivottsxvoqqaqdxiviidivndvdtbvadnxboiqivpusuxaaqnqaobutdbpiosuitdnopoboivopaapadvqwwnnwvxndpxbapixaspwxxxvppoptqxitsvaaawxwaxtbxuixsoxoqdtopqqivaitnpvutzchkygjjgjkcfzjzrkmyerhgkglcyffezmehjcllmlrjghhfkfylkgyhyjfmljkzglkklykrjgrmzjyeyzrrkymccefggczrjflykclfhrjjckjlmglrmgfzlkkhffkjrkyfhegyykrzgjzcgjhkzzmzyejycfrkkekmhzjgggrmchkeclljlyhjkchmhjlehhejjyccyegzrcrerfzczfelzrlfylzleefgefgmzzlggmejjjygehmrczmkrc"));
    }
}
