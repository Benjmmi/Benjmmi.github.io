package leetcode;


import java.util.HashMap;
import java.util.Map;

/**
 * 给定两个字符串 s 和 t ，判断它们是否是同构的。
 *
 * 如果 s 中的字符可以按某种映射关系替换得到 t ，那么这两个字符串是同构的。
 *
 * 每个出现的字符都应当映射到另一个字符，同时不改变字符的顺序。不同字符不能映射到同一个字符上，相同字符只能映射到同一个字符上，字符可以映射到自己本身。
 *
 *
 *
 * 示例 1:
 *
 * 输入：s = "egg", t = "add"
 * 输出：true
 * 示例 2：
 *
 * 输入：s = "foo", t = "bar"
 * 输出：false
 * 示例 3：
 *
 * 输入：s = "paper", t = "title"
 * 输出：true
 *
 *
 * 提示：
 *
 * 1 <= s.length <= 5 * 104
 * t.length == s.length
 * s 和 t 由任意有效的 ASCII 字符组成
 */
public class L_205 {
    public boolean isIsomorphic(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        if (s.equals(t)) {
            return true;
        }
        char[] charS = s.toCharArray();
        char[] charT = t.toCharArray();

        Map<Character, Character> mapST = new HashMap<>();
        Map<Character, Character> mapTS = new HashMap<>();

        for (int i = 0; i < charS.length; i++) {
            char cs = charS[i];
            char ct = charT[i];
            {
                Character value = mapST.get(cs);
                if (value == null) {
                    mapST.put(cs, ct);
                } else if (value != ct) {
                    return false;
                }
            }
            {
                Character value = mapTS.get(ct);
                if (value == null) {
                    mapTS.put(ct, cs);
                } else if (value != cs) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void main(String[] args) {
        L_205 l205 = new L_205();
        {
            System.out.println(l205.isIsomorphic("badc", "baba"));
        }
        {
            System.out.println(l205.isIsomorphic("egg", "add"));
        }
        {
            System.out.println(l205.isIsomorphic("foo", "bar"));
        }
        {
            System.out.println(l205.isIsomorphic("abc", "efg"));
        }
        {
            System.out.println(l205.isIsomorphic("a", "a"));
        }
        {
            System.out.println(l205.isIsomorphic("paper", "title"));
        }
    }
}
