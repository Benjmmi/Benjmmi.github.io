package leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * 给你一个字符串 s 、一个字符串 t 。返回 s 中涵盖 t 所有字符的最小子串。如果 s 中不存在涵盖 t 所有字符的子串，则返回空字符串 "" 。
 *
 *
 *
 * 注意：
 *
 * 对于 t 中重复字符，我们寻找的子字符串中该字符数量必须不少于 t 中该字符数量。
 * 如果 s 中存在这样的子串，我们保证它是唯一的答案。
 *
 *
 * 示例 1：
 *
 * 输入：s = "ADOBECODEBANC", t = "ABC"
 * 输出："BANC"
 * 解释：最小覆盖子串 "BANC" 包含来自字符串 t 的 'A'、'B' 和 'C'。
 * 示例 2：
 *
 * 输入：s = "a", t = "a"
 * 输出："a"
 * 解释：整个字符串 s 是最小覆盖子串。
 * 示例 3:
 *
 * 输入: s = "a", t = "aa"
 * 输出: ""
 * 解释: t 中两个字符 'a' 均应包含在 s 的子串中，
 * 因此没有符合条件的子字符串，返回空字符串。
 *
 *
 * 提示：
 *
 * m == s.length
 * n == t.length
 * 1 <= m, n <= 105
 * s 和 t 由英文字母组成
 */
public class L_76 {
    int[] tHash = new int[128];


    public String minWindow(String s, String t) {

        if (s.length() < t.length()) {
            return "";
        }

        if (s.contains(t)) {
            return t;
        }

        // 确认首次初始化窗口覆盖范围
        // 左指针向右地推，推动右指针移动
        //
        char[] charS = s.toCharArray();
        char[] charT = t.toCharArray();

        Map<Character, Integer> gHash = new HashMap<>();
        for (char c : charT) {
            tHash[c] += 1;
            gHash.put(c, gHash.getOrDefault(c, 0) + 1);
        }
        int l = 0;
        int r = charS.length - 1;
        for (; l <= r; l++) {
            if (tHash[charS[l]] > 0) {
                break;
            }
        }
        for (; r > l; r--) {
            if (tHash[charS[r]] > 0) {
                break;
            }
        }
        if (r - l + 1 < t.length()) {
            return "";
        }

        // 找到首个距离
        int i = l;
        for (; i <= r; i++) {
            char c = s.charAt(i);
            if (tHash[c] > 0 && gHash.containsKey(c)) {
                gHash.put(c, gHash.get(c) - 1);
            }
            if (gHash.getOrDefault(c, 0) == 0) {
                gHash.remove(c);
            }
            if (gHash.isEmpty()) {
                break;
            }
        }

        if (i > r) {
            return "";
        }

        // 统计数量
        for (int j = l; j <= i; j++) {
            char c = s.charAt(j);
            if (tHash[c] > 0) {
                gHash.put(c, gHash.getOrDefault(c, 0) + 1);
            }
        }

        String result = s.substring(l, i + 1);

        int pre = l;
        int last = i;
        int j = pre;
        for (; j <= last; j++) {
            char c = charS[j];
            if (tHash[c] > 0 && gHash.get(c) - 1 < tHash[c]) {
                result = substring(result, s, j, last + 1);
                int newLast = findNextC(charS[j], last + 1, r, charS, gHash);
                if (newLast < 0) {
                    break;
                }
                last = newLast;
            }
            if (tHash[c] > 0) {
                gHash.put(c, gHash.get(c) - 1);
            }
            result = substring(result, s, j, last + 1);
        }

        return result;
    }

    public String substring(String result, String s, int start, int end) {
        String bak = s.substring(start, end);
        if (bak.length() < result.length()) {
            result = bak;
        }
        return result;
    }

    public int findNextC(char c, int startIndex, int endIndex, char[] charS, Map<Character, Integer> gHash) {
        for (int i = startIndex; i <= endIndex; i++) {
            char bb = charS[i];
            if (tHash[bb] > 0) {
                gHash.put(bb, gHash.get(bb) + 1);
            }
            if (bb == c) {
                return i;
            }
        }
        return -1;
    }

//    {
//
//        if (s.length() < t.length()) {
//            return "";
//        }
//
//        if (s.contains(t)) {
//            return t;
//        }
//
//        // 确认首次初始化窗口覆盖范围
//        // 左指针向右地推，推动右指针移动
//        //
//
//        Map<Character, Integer> sIntegerMap = new HashMap<>();
//        Map<Character, Integer> tIntegerMap = new HashMap<>();
//
//        for (int i = 0; i < s.length(); i++) {
//            sIntegerMap.put(s.charAt(i), sIntegerMap.getOrDefault(s.charAt(i), 0) + 1);
//        }
//
//        for (int i = 0; i < t.length(); i++) {
//            tIntegerMap.put(t.charAt(i), tIntegerMap.getOrDefault(t.charAt(i), 0) + 1);
//            if (tIntegerMap.get(t.charAt(i)) > sIntegerMap.getOrDefault(t.charAt(i), 0)) {
//                return "";
//            }
//        }
//
//        int i = 0;
//        for (; i < s.length(); i++) {
//            if (sIntegerMap.get(s.charAt(i)) > tIntegerMap.getOrDefault(s.charAt(i), 0)) {
//                sIntegerMap.put(s.charAt(i), sIntegerMap.getOrDefault(s.charAt(i), 0) - 1);
//            } else {
//                break;
//            }
//        }
//        int j = s.length() - 1;
//        for (; j > i; j--) {
//            if (sIntegerMap.get(s.charAt(j)) > tIntegerMap.getOrDefault(s.charAt(j), 0)) {
//                sIntegerMap.put(s.charAt(j), sIntegerMap.getOrDefault(s.charAt(j), 0) - 1);
//            } else {
//                break;
//            }
//        }
//
//        return s.substring(i, j + 1);
//    }

    public static void main(String[] args) {
        L_76 l76 = new L_76();

        {
            System.out.println(l76.minWindow("cabwefgewcwaefgcf", "cae")); // cwae aefgc
        }
        {
            System.out.println(l76.minWindow("bba", "ab"));
        }
        {
            System.out.println(l76.minWindow("aaaaaaaaaaaabbbbbcdd", "abcdd")); // abbbbbcdd
        }

        {
            System.out.println(l76.minWindow("ADOBECODEBANC", "ABC"));
        }


        {
            System.out.println(l76.minWindow("abc", "cba")); // abc
        }
        {
            System.out.println(l76.minWindow("a", "a"));
        }
        {
            System.out.println(l76.minWindow("a", "aa"));
        }
        {
            System.out.println(l76.minWindow("aaa", "aa"));
        }
        {
            System.out.println(l76.minWindow("aaaaaaaaaaaaabc", "abc"));
        }
        {
            System.out.println(l76.minWindow("acbbbbbbbbbbbbbbb", "abc"));
        }
        {
            System.out.println(l76.minWindow("aaccabaaaa", "ab"));
        }

    }
}
