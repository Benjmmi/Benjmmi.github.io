package leetcode;

import jdk.nashorn.internal.runtime.Scope;

import java.util.*;

/**
 * 以数组 intervals 表示若干个区间的集合，其中单个区间为 intervals[i] = [starti, endi] 。
 * 请你合并所有重叠的区间，并返回 一个不重叠的区间数组，该数组需恰好覆盖输入中的所有区间 。
 *
 *
 *
 * 示例 1：
 *
 * 输入：intervals = [[1,3],[2,6],[8,10],[15,18]]
 * 输出：[[1,6],[8,10],[15,18]]
 * 解释：区间 [1,3] 和 [2,6] 重叠, 将它们合并为 [1,6].
 * 示例 2：
 *
 * 输入：intervals = [[1,4],[4,5]]
 * 输出：[[1,5]]
 * 解释：区间 [1,4] 和 [4,5] 可被视为重叠区间。
 *
 *
 * 提示：
 *
 * 1 <= intervals.length <= 104
 * intervals[i].length == 2
 * 0 <= starti <= endi <= 104
 */
public class L_56 {
    public int[][] merge(int[][] intervals) {
        if (intervals.length == 1) {
            return intervals;
        }
        // start < start1 < end
        // start < end1 < end


        Arrays.sort(intervals, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        });
        int start = intervals[0][0];
        int end = intervals[0][1];
        List<int[]> temp = new ArrayList<>();
        for (int i = 1; i < intervals.length; i++) {
            if (
                    (start <= intervals[i][0] && intervals[i][0] <= end)
                            || (start <= intervals[i][1] && intervals[i][1] <= end)
            ) {
                start = Math.min(start, intervals[i][0]);
                end = Math.max(end, intervals[i][1]);
            } else {
                int[] result = new int[2];
                result[0] = start;
                result[1] = end;
                temp.add(result);
                start = intervals[i][0];
                end = intervals[i][1];
            }
        }
        {
            int[] result = new int[2];
            result[0] = start;
            result[1] = end;
            temp.add(result);
        }

        int[][] result = new int[temp.size()][2];
        int scope = 0;
        for (int[] ints : temp) {
            result[scope] = ints;
            scope++;
        }

        return result;
    }

    public static void main(String[] args) {
        L_56 l56 = new L_56();
        {
            System.out.println(l56.merge(new int[][]{{1, 4}, {2, 3}}));
        }
        {
            System.out.println(l56.merge(new int[][]{{1, 4}, {0, 1}}));
        }
        {
            System.out.println(l56.merge(new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}}));
        }
        {
            System.out.println(l56.merge(new int[][]{{1, 4}, {4, 5}}));
        }
    }
}
