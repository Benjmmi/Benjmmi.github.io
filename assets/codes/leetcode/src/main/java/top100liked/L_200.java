package top100liked;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class L_200 {


    public int numIslands(char[][] grid) {
        int[][] guide = new int[][]{
                {0, -1},
                {-1, 0},
                {1, 0},
                {0, 1},
        };

        int nums = 0;
        Queue<int[]> queue = new LinkedBlockingQueue<>();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == '1') {
                    queue.offer(new int[]{i, j});
                    grid[i][j] = '0';
                    nums++;
                }
                while (!queue.isEmpty()) {
                    int[] curr = queue.poll();
                    for (int[] ints : guide) {
                        int x = curr[0] + ints[0];
                        int y = curr[1] + ints[1];
                        if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length) {
                            continue;
                        }
                        if (grid[x][y] == '1') {
                            queue.offer(new int[]{x, y});
                            grid[x][y] = '0';
                        }
                    }
                }
            }
        }
        return nums;
    }

    public static void main(String[] args) {

    }
}
