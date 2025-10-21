package top100liked;

import java.util.LinkedList;
import java.util.Queue;

public class L_994 {
    public int orangesRotting(int[][] grid) {
        int step = 0;
        int[][] points = new int[][]{
                {1, 0},
                {0, 1},
                {0, -1},
                {-1, 0},
        };
        Queue<int[]> queue = new LinkedList<>();

        int n = grid.length;
        int m = grid[0].length;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == 2) {
                    queue.offer(new int[]{i, j});
                }
            }
        }

        while (!queue.isEmpty()) {
            boolean flag = false;
            int size = queue.size();
            for (int k = 0; k < size; k++) {
                int[] point = queue.poll();

                for (int[] ints : points) {
                    assert point != null;
                    int x = point[0] + ints[0];
                    int y = point[1] + ints[1];
                    if (x < 0 || x >= n || y < 0 || y >= m) {
                        continue;
                    }
                    if (grid[x][y] == 1) {
                        queue.offer(new int[]{x, y});
                        grid[x][y] = 0;
                        flag = true;
                    }
                }
            }
            if (flag) {
                step++;
            }
        }


        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == 1) {
                    return -1;
                }
            }
        }

        return step == -1 ? -0 : step;
    }


    public static void main(String[] args) {
        L_994 l994 = new L_994();
        System.out.println(l994.orangesRotting(new int[][]{{2, 1, 1}, {1, 1, 1}, {0, 1, 2}}));
    }
}
