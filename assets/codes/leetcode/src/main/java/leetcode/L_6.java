package leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * 将一个给定字符串 s 根据给定的行数 numRows ，以从上往下、从左到右进行 Z 字形排列。
 *
 * 比如输入字符串为 "PAYPALISHIRING" 行数为 3 时，排列如下：
 *
 * P   A   H   N
 * A P L S I I G
 * Y   I   R
 * 之后，你的输出需要从左往右逐行读取，产生出一个新的字符串，比如："PAHNAPLSIIGYIR"。
 *
 * 请你实现这个将字符串进行指定行数变换的函数：
 *
 * string convert(string s, int numRows);
 *
 *
 * 示例 1：
 *
 * 输入：s = "PAYPALISHIRING", numRows = 3
 * 输出："PAHNAPLSIIGYIR"
 * 示例 2：
 * 输入：s = "PAYPALISHIRING", numRows = 4
 * 输出："PINALSIGYAHRPI"
 * 解释：
 * P     I    N
 * A   L S  I G
 * Y A   H R
 * P     I
 * 示例 3：
 *
 * 输入：s = "A", numRows = 1
 * 输出："A"
 *
 *
 * 提示：
 *
 * 1 <= s.length <= 1000
 * s 由英文字母（小写和大写）、',' 和 '.' 组成
 * 1 <= numRows <= 1000
 */
public class L_6 {
    public String convert(String s, int numRows) {
        if (numRows == 1) {
            return s;
        }
        int middle = numRows - 2;
        StringBuilder builder = new StringBuilder();
        // fist   middle*2+1
        // second  (middle-1)*2+1
        // third  (middle-2)*2+1
        // ...
        // last  (middle*2)+1
        List<StringBuilder> list = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            list.add(new StringBuilder());
        }
        boolean swi = true;
        for (int i = 0, j = 0; i < s.length(); i++) {
            list.get(j).append(s.charAt(i));
            if (swi) {
                j++;
            } else {
                j--;
            }
            if (j == 0) {
                swi = true;
            }
            if (j == numRows - 1) {
                swi = false;
            }
        }
        StringBuilder result = new StringBuilder();
        for (StringBuilder item : list) {
            result.append(item);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        L_6 l6 = new L_6();
        {
            System.out.println(l6.convert("PAYPALISHIRING", 3));
        }
        {
            System.out.println(l6.convert("PAYPALISHIRING", 4));
        }
    }
}
