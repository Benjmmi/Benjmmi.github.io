package leetcode;

import java.util.*;

/**
 * 给定一个字符串 s ，请你找出其中不含有重复字符的 最长 子串 的长度。
 *
 *
 *
 * 示例 1:
 *
 * 输入: s = "abcabcbb"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
 * 示例 2:
 *
 * 输入: s = "bbbbb"
 * 输出: 1
 * 解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。
 * 示例 3:
 *
 * 输入: s = "pwwkew"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
 *      请注意，你的答案必须是 子串 的长度，"pwke" 是一个子序列，不是子串。
 *
 *
 * 提示：
 *
 * 0 <= s.length <= 5 * 104
 * s 由英文字母、数字、符号和空格组成
 */
public class L_3 {

    public int lengthOfLongestSubstring(String s) {
        if (s.length() <= 1) {
            return s.length();
        }
        // 滑动窗口解法
        Set<Character> characters = new HashSet<>();
        char[] chars = s.toCharArray();
        int pre = 0;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < chars.length; i++) {
            max = Math.max(max, i - pre);
            if (characters.contains(chars[i])) {
                int j = pre;
                while (characters.contains(chars[i])) {
                    characters.remove(chars[j]);
                    j++;
                }
                pre = j;
            }
            characters.add(chars[i]);
        }

        return Math.max(max, characters.size());
    }

//    {
//
//        if (s.length() <= 1) {
//            return s.length();
//        }
//        ArrayDeque<Character> characters = new ArrayDeque<>();
//        char[] chars = s.toCharArray();
//        int max = Integer.MIN_VALUE;
//        for (char aChar : chars) {
//            if (characters.contains(aChar)) {
//                max = Math.max(max, characters.size());
//                while (characters.removeLast() != aChar) {
//
//                }
//            }
//            characters.push(aChar);
//        }
//        return max < characters.size() ? characters.size() : max;
//    }

    public static void main(String[] args) {
        L_3 l3 = new L_3();
        {
            System.out.println(l3.lengthOfLongestSubstring("au")); // 3
        }
        {
            System.out.println(l3.lengthOfLongestSubstring("abcabcbb")); // 3
        }
        {
            System.out.println(l3.lengthOfLongestSubstring("bbbbb")); // 1
        }
        {
            System.out.println(l3.lengthOfLongestSubstring("pwwkew")); // 3
        }
        {
            System.out.println(l3.lengthOfLongestSubstring("dvdf")); // 3
        }
    }
}
