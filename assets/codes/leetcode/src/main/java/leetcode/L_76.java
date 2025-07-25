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

        Map<Character, Integer> sIntegerMap = new HashMap<>();
        Map<Character, Integer> tIntegerMap = new HashMap<>();

        for (int i = 0; i < s.length(); i++) {
            sIntegerMap.put(s.charAt(i), sIntegerMap.getOrDefault(s.charAt(i), 0) + 1);
        }

        for (int i = 0; i < t.length(); i++) {
            tIntegerMap.put(t.charAt(i), tIntegerMap.getOrDefault(t.charAt(i), 0) + 1);
            if (tIntegerMap.get(t.charAt(i)) > sIntegerMap.getOrDefault(t.charAt(i), 0)) {
                return "";
            }
        }

        int i = 0;
        for (; i < s.length(); i++) {
            if (sIntegerMap.get(s.charAt(i)) > tIntegerMap.getOrDefault(s.charAt(i), 0)) {
                sIntegerMap.put(s.charAt(i), sIntegerMap.getOrDefault(s.charAt(i), 0) - 1);
            } else {
                break;
            }
        }
        int j = s.length() - 1;
        for (; j > i; j--) {
            if (sIntegerMap.get(s.charAt(j)) > tIntegerMap.getOrDefault(s.charAt(j), 0)) {
                sIntegerMap.put(s.charAt(j), sIntegerMap.getOrDefault(s.charAt(j), 0) - 1);
            } else {
                break;
            }
        }

        return s.substring(i, j + 1);
    }

    public static void main(String[] args) {
        L_76 l76 = new L_76();
        {
            System.out.println(l76.minWindow("ADOBECODEBANC", "ABC"));
        }
        {
            System.out.println(l76.minWindow("a", "a"));
        }
        {
            System.out.println(l76.minWindow("a", "aa"));
        }
        {
            System.out.println(l76.minWindow("cabwefgewcwaefgcf", "cae")); // cwae aefgc
        }
    }
}
