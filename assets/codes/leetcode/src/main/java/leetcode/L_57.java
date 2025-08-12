package leetcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 给你一个 无重叠的 ，按照区间起始端点排序的区间列表 intervals，其中 intervals[i] = [starti, endi] 表示第 i 个区间的开始和结束，并且 intervals 按照 starti 升序排列。同样给定一个区间 newInterval = [start, end] 表示另一个区间的开始和结束。
 *
 * 在 intervals 中插入区间 newInterval，使得 intervals 依然按照 starti 升序排列，且区间之间不重叠（如果有必要的话，可以合并区间）。
 *
 * 返回插入之后的 intervals。
 *
 * 注意 你不需要原地修改 intervals。你可以创建一个新数组然后返回它。
 *
 *
 *
 * 示例 1：
 *
 * 输入：intervals = [[1,3],[6,9]], newInterval = [2,5]
 * 输出：[[1,5],[6,9]]
 * 示例 2：
 *
 * 输入：intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
 * 输出：[[1,2],[3,10],[12,16]]
 * 解释：这是因为新的区间 [4,8] 与 [3,5],[6,7],[8,10] 重叠。
 *
 *
 * 提示：
 *
 * 0 <= intervals.length <= 104
 * intervals[i].length == 2
 * 0 <= starti <= endi <= 105
 * intervals 根据 starti 按 升序 排列
 * newInterval.length == 2
 * 0 <= start <= end <= 105
 */
public class L_57 {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> temp = new ArrayList<>();
        boolean isInsert = false;
        for (int[] interval : intervals) {
            if (interval[0] >= newInterval[0] && !isInsert) {
                temp.add(newInterval);
                isInsert = true;
            }
            temp.add(interval);
        }

        if (!isInsert) {
            temp.add(newInterval);
        }

        List<int[]> result = new ArrayList<>();
        int start = Integer.MAX_VALUE;
        int end = Integer.MIN_VALUE;


        for (int[] ints : temp) {
            if (
                    (start <= ints[0] && ints[0] <= end)
                            || (start <= ints[1] && ints[1] <= end)
            ) {
                start = Math.min(ints[0], start);
                end = Math.max(ints[1], end);
            } else {
                if (start == Integer.MAX_VALUE) {
                    start = ints[0];
                    end = ints[1];
                    continue;
                }
                result.add(new int[]{start, end});
                start = ints[0];
                end = ints[1];
            }
        }
        if (start != Integer.MAX_VALUE) {
            result.add(new int[]{start, end});
        }

        intervals = new int[result.size()][2];
        for (int i = 0; i < result.size(); i++) {
            intervals[i][0] = result.get(i)[0];
            intervals[i][1] = result.get(i)[1];
        }

        return intervals;
    }

    public static void main(String[] args) {
        L_57 l57 = new L_57();
        {
            l57.insert(new int[][]{}, new int[]{5, 7});
        }
        {
            l57.insert(new int[][]{{1, 3}, {6, 9}}, new int[]{2, 5});
        }
        {
            l57.insert(new int[][]{{1, 2}, {3, 5}, {6, 7}, {8, 10}, {12, 16}}, new int[]{4, 8});
        }
    }
}
