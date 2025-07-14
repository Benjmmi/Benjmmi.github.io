package leetcode;

/**
 * 编写一个函数来查找字符串数组中的最长公共前缀。
 *
 * 如果不存在公共前缀，返回空字符串 ""。
 *
 *
 *
 * 示例 1：
 *
 * 输入：strs = ["flower","flow","flight"]
 * 输出："fl"
 * 示例 2：
 *
 * 输入：strs = ["dog","racecar","car"]
 * 输出：""
 * 解释：输入不存在公共前缀。
 *
 *
 * 提示：
 *
 * 1 <= strs.length <= 200
 * 0 <= strs[i].length <= 200
 * strs[i] 如果非空，则仅由小写英文字母组成
 */
public class L_14 {

    public String longestCommonPrefix(String[] strs) {
        String commonPrefix = strs[0];
        for (int i = 1; i < strs.length; i++) {
            int end = 0;
            for (int j = 0; j < commonPrefix.length(); j++) {
                if (j >= strs[i].length()) {
                    break;
                }
                if (commonPrefix.charAt(j) != strs[i].charAt(j)) {
                    break;
                }
                end++;
            }
            commonPrefix = commonPrefix.substring(0, end);
            if (commonPrefix.isEmpty()) {
                break;
            }
        }
        return commonPrefix;
    }

    public static void main(String[] args) {
        L_14 l14 = new L_14();
        {
            System.out.println(l14.longestCommonPrefix(new String[]{"flower", "flow", "flight"}));
        }
        {
            System.out.println(l14.longestCommonPrefix(new String[]{"flower", "", "flight"}));
        }
        {
            System.out.println(l14.longestCommonPrefix(new String[]{"dog", "racecar", "car"}));
        }
        {
            System.out.println(l14.longestCommonPrefix(new String[]{"", "racecar", "car"}));
        }
    }
}
