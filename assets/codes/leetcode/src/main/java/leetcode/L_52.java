package leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class L_52 {

    public int totalNQueens(int n) {
        List<List<String>> answer = new ArrayList<>();
        dsf(0, new int[n], new boolean[n], new boolean[n * 2 - 1], new boolean[n * 2 - 1], answer);
        return answer.size();
    }


    public void dsf(int r, int[] queues, boolean[] col, boolean[] queueYAdd, boolean[] queueYSub, List<List<String>> answer) {
        int n = col.length;
        if (r == n) {
            List<String> ans = new ArrayList<>();
            for (int queue : queues) {
                char[] chars = new char[n];
                Arrays.fill(chars, '.');
                chars[queue] = 'Q';
                ans.add(new String(chars));
            }
            answer.add(ans);
            return;
        }

        for (int c = 0; c < n; c++) {
            // 副对角线（右上到左下 /），行号与列号的差值 `r-c` 是固定的
            // 加上 n-1 是为了将负数索引转为正数
            int lu = r - c + n - 1;
            // 主对角线是 和值是一样的
            int ru = r + c;
            // 确认 列上没有，确定 左斜线上 和 右斜线上 没有
            if (!col[c] && !queueYAdd[ru] && !queueYSub[lu]) {
                queues[r] = c;
                col[c] = queueYAdd[ru] = queueYSub[lu] = true;
                dsf(r + 1, queues, col, queueYAdd, queueYSub, answer);
                col[c] = queueYAdd[ru] = queueYSub[lu] = false;
            }
        }
    }


    public static void main(String[] args) {

    }
}
