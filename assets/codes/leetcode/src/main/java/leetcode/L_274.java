package leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 给你一个整数数组 citations ，其中 citations[i] 表示研究者的第 i 篇论文被引用的次数。计算并返回该研究者的 h 指数。
 *
 * 根据维基百科上 h 指数的定义：h 代表“高引用次数” ，一名科研人员的 h 指数 是指他（她）至少发表了 h 篇论文，
 * 并且 至少 有 h 篇论文被引用次数大于等于 h 。如果 h 有多种可能的值，h 指数 是其中最大的那个。
 *
 *
 *
 * 示例 1：
 *
 * 输入：citations = [3,0,6,1,5]
 * 输出：3
 * 解释：给定数组表示研究者总共有 5 篇论文，每篇论文相应的被引用了 3, 0, 6, 1, 5 次。
 *      由于研究者有 3 篇论文每篇 至少 被引用了 3 次，其余两篇论文每篇被引用 不多于 3 次，所以她的 h 指数是 3。
 * 示例 2：
 *
 * 输入：citations = [1,3,1]
 * 输出：1
 *
 *
 * 提示：
 *
 * n == citations.length
 * 1 <= n <= 5000
 * 0 <= citations[i] <= 1000
 *
 * 思路：1
 * 对数组排序，取中位数
 * 思路：2
 * map 字典统计
 * 思路3：
 * O(1) N(1)
 *
 *
 */
public class L_274 {
    public int hIndex(int[] citations) {
        Arrays.sort(citations);
        int max = 0;
        for (int i = citations.length - 1; i >= 0; i--) {
            if (citations[i] > max) {
                max++;
            } else {
                break;
            }
        }
        return max;
    }

    public static void main(String[] args) {
        L_274 l274 = new L_274();
        {
            int[] citations = {3, 0, 6, 1, 5};
            int result = l274.hIndex(citations);
            System.out.println("H-Index: " + result); // 输出: H-Index: 3
        }
        {
            int[] citations = {1, 3, 1};
            int result = l274.hIndex(citations);
            System.out.println("H-Index: " + result); // 输出: H-Index: 1
        }
        {
            int[] citations = {94, 5, 3, 1, 3, 5, 2, 35, 5, 43, 23, 45, 3, 3, 4, 42, 2, 11, 1, 1, 1, 1, 11, 1, 1, 1, 1, 1, 1, 1, 1, 11, 1, 1};
            int result = l274.hIndex(citations);
            System.out.println("H-Index: " + result); // 输出: H-Index: 9
        }
    }
}
