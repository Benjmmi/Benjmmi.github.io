package leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * 给定一个单词数组 words 和一个长度 maxWidth ，重新排版单词，使其成为每行恰好有 maxWidth 个字符，且左右两端对齐的文本。
 *
 * 你应该使用 “贪心算法” 来放置给定的单词；也就是说，尽可能多地往每行中放置单词。必要时可用空格 ' ' 填充，使得每行恰好有 maxWidth 个字符。
 *
 * 要求尽可能均匀分配单词间的空格数量。如果某一行单词间的空格不能均匀分配，则左侧放置的空格数要多于右侧的空格数。
 *
 * 文本的最后一行应为左对齐，且单词之间不插入额外的空格。
 *
 * 注意:
 *
 * 单词是指由非空格字符组成的字符序列。
 * 每个单词的长度大于 0，小于等于 maxWidth。
 * 输入单词数组 words 至少包含一个单词。
 *
 *
 * 示例 1:
 *
 * 输入: words = ["This", "is", "an", "example", "of", "text", "justification."], maxWidth = 16
 * 输出:
 * [
 *    "This    is    an",
 *    "example  of text",
 *    "justification.  "
 * ]
 * 示例 2:
 *
 * 输入:words = ["What","must","be","acknowledgment","shall","be"], maxWidth = 16
 * 输出:
 * [
 *   "What   must   be",
 *   "acknowledgment  ",
 *   "shall be        "
 * ]
 * 解释: 注意最后一行的格式应为 "shall be    " 而不是 "shall     be",
 *      因为最后一行应为左对齐，而不是左右两端对齐。
 *      第二行同样为左对齐，这是因为这行只包含一个单词。
 * 示例 3:
 *
 * 输入:words = ["Science","is","what","we","understand","well","enough","to","explain","to","a","computer.","Art","is","everything","else","we","do"]，maxWidth = 20
 * 输出:
 * [
 *   "Science  is  what we",
 *   "understand      well",
 *   "enough to explain to",
 *   "a  computer.  Art is",
 *   "everything  else  we",
 *   "do                  "
 * ]
 *
 *
 * 提示:
 *
 * 1 <= words.length <= 300
 * 1 <= words[i].length <= 20
 * words[i] 由小写英文字母和符号组成
 * 1 <= maxWidth <= 100
 * words[i].length <= maxWidth
 */
public class L_68 {
    public List<String> fullJustify(String[] words, int maxWidth) {

        List<String> list = new ArrayList<>();
        // 单词长度 相加，如果加上单词数量超过 maxWidth 则回退一个单词
        // 处理好 临界值

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (builder.length() == 0) {
                builder.append(words[i]);
            } else if (builder.length() + words[i].length() + 1 <= maxWidth) {
                builder.append(" ").append(words[i]);
            } else {
                int needInsert = builder.indexOf(" ");
                if (needInsert > 0) {
                    int fromIndex = 0;
                    while (maxWidth > builder.length()) {
                        int index = builder.indexOf(" ", fromIndex);
                        if (index < 0) {
                            fromIndex = 0;
                            continue;
                        }
                        builder.insert(index, " ");
                        for (int j = index; j < builder.length(); j++) {
                            if (builder.charAt(j) != ' ') {
                                fromIndex = j;
                                break;
                            }
                        }

                    }
                } else {
                    while (builder.length() < maxWidth) {
                        builder.append(" ");
                    }
                }

                list.add(builder.toString());
                i--;
                builder = new StringBuilder();
            }
        }
        if (builder.length() != 0) {
            while (builder.length() < maxWidth) {
                builder.append(" ");
            }
            list.add(builder.toString());
        }
        return list;
    }

    public static void main(String[] args) {
        L_68 l68 = new L_68();
        {
            List<String> result = l68.fullJustify(new String[]{"This", "is", "an", "example", "of", "text", "justification."}, 16);
            System.out.println(result);
        }
        {
            List<String> result = l68.fullJustify(new String[]{"What", "must", "be", "acknowledgment", "shall", "be"}, 16);
            System.out.println(result);
        }
        {
            List<String> result = l68.fullJustify(new String[]{"Science", "is", "what", "we", "understand", "well", "enough", "to", "explain", "to", "a", "computer.", "Art", "is", "everything", "else", "we", "do"}, 20);
            System.out.println(result);
        }
    }
}
