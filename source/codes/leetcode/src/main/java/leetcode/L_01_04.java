package leetcode;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class L_01_04 {
    public static void main(String[] args) {
        new L_01_04().canPermutePalindrome("sfsda");
    }

    public boolean canPermutePalindrome(String s) {
        Set<Character> b = new HashSet<Character>();
        for (char c : s.toCharArray()) {
            if (b.contains(c)){
                b.remove(c);
            }else{
                b.add(c);
            }
        }
        TreeSet set = new TreeSet();
        set.ceiling()
        return b.size() < 1;
    }
}
