package leetcode;

import java.util.Arrays;

public class L_64 {

    public int sumNums(int n) {
        int sum = n;
        boolean al = (n != 0) && (sum = sum + sumNums(n - 1)) >= 0;
        return sum;
    }

    public int minPathSum(int[][] grid) {
//        dsf(grid, 0, 0, grid[0][0]);
        int[][] sum = new int[grid.length][grid[0].length];
        for (int[] ints : sum) {
            Arrays.fill(ints, Integer.MAX_VALUE);
        }
        sum[0][0] = grid[0][0];
        dsf(grid, 0, 0, sum);
        return sum[grid.length - 1][grid[0].length - 1];
    }

//    public void dsf(int[][] grid, int row, int col, int sum) {
//        if (row == grid.length - 1 && col == grid[0].length - 1) {
//            min = Math.min(min, sum);
//            return;
//        }
//
//        if (row + 1 < grid.length) {
//            int down = grid[row + 1][col];
//            dsf(grid, row + 1, col, down + sum);
//        }
//        if (col + 1 < grid[0].length) {
//            int right = grid[row][col + 1];
//            dsf(grid, row, col + 1, right + sum);
//        }
//    }

    public void dsf(int[][] grid, int row, int col, int[][] sum) {
        if (row == grid.length || col == grid[0].length) {
            return;
        }
        int curSum = sum[row][col];
        if (row + 1 < grid.length) {
            int downSum = sum[row + 1][col];
            if (curSum + grid[row + 1][col] <= downSum) {
                sum[row + 1][col] = curSum + grid[row + 1][col];
                dsf(grid, row + 1, col, sum);
            }
        }

        if (col + 1 < grid[0].length) {
            int rightSum = sum[row][col + 1];
            if (curSum + grid[row][col + 1] <= rightSum) {
                sum[row][col + 1] = curSum + grid[row][col + 1];
                dsf(grid, row, col + 1, sum);
            }
        }
    }


    public static void main(String[] args) {
//        int a = new L_64().sumNums(2);
//        System.out.println(a);
        L_64 l = new L_64();
        System.out.println(l.minPathSum(new int[][]{
                {1, 3, 1},
                {1, 5, 1},
                {4, 2, 1}}));
        System.out.println(l.minPathSum(new int[][]{
                {1, 2, 3},
                {4, 5, 6}}));
        System.out.println(l.minPathSum(new int[][]{
                {3, 8, 6, 0, 5, 9, 9, 6, 3, 4, 0, 5, 7, 3, 9, 3},
                {0, 9, 2, 5, 5, 4, 9, 1, 4, 6, 9, 5, 6, 7, 3, 2},
                {8, 2, 2, 3, 3, 3, 1, 6, 9, 1, 1, 6, 6, 2, 1, 9},
                {1, 3, 6, 9, 9, 5, 0, 3, 4, 9, 1, 0, 9, 6, 2, 7},
                {8, 6, 2, 2, 1, 3, 0, 0, 7, 2, 7, 5, 4, 8, 4, 8},
                {4, 1, 9, 5, 8, 9, 9, 2, 0, 2, 5, 1, 8, 7, 0, 9},
                {6, 2, 1, 7, 8, 1, 8, 5, 5, 7, 0, 2, 5, 7, 2, 1},
                {8, 1, 7, 6, 2, 8, 1, 2, 2, 6, 4, 0, 5, 4, 1, 3},
                {9, 2, 1, 7, 6, 1, 4, 3, 8, 6, 5, 5, 3, 9, 7, 3},
                {0, 6, 0, 2, 4, 3, 7, 6, 1, 3, 8, 6, 9, 0, 0, 8},
                {4, 3, 7, 2, 4, 3, 6, 4, 0, 3, 9, 5, 3, 6, 9, 3},
                {2, 1, 8, 8, 4, 5, 6, 5, 8, 7, 3, 7, 7, 5, 8, 3},
                {0, 7, 6, 6, 1, 2, 0, 3, 5, 0, 8, 0, 8, 7, 4, 3},
                {0, 4, 3, 4, 9, 0, 1, 9, 7, 7, 8, 6, 4, 6, 9, 5},
                {6, 5, 1, 9, 9, 2, 2, 7, 4, 2, 7, 2, 2, 3, 7, 2},
                {7, 1, 9, 6, 1, 2, 7, 0, 9, 6, 6, 4, 4, 5, 1, 0},
        }));
    }
}
