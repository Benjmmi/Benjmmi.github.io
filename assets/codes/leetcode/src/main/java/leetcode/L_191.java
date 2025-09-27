package leetcode;

public class L_191 {
    public int hammingWeight(int n) {
        String s = Integer.toBinaryString(n);
        s = s.replace("0","");
        return s.length();
    }

    public static void main(String[] args) {

    }
}
