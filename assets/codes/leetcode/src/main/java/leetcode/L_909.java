package leetcode;

import java.util.ArrayList;
import java.util.List;

public class L_909 {
    public int snakesAndLadders(int[][] board) {
        int n = board.length;
        boolean[] vis = new boolean[n * n + 1];
        vis[1] = true; // 题目保证起点没有蛇梯，不写也可以
        List<Integer> q = new ArrayList<>();
        q.add(1); // 起点
        for (int step = 0; !q.isEmpty(); step++) {
            List<Integer> tmp = q;
            q = new ArrayList<>();
            for (int x : tmp) {
                if (x == n * n) { // 终点
                    return step;
                }
                for (int y = x + 1; y <= Math.min(x + 6, n * n); y++) {
                    int r = (y - 1) / n;
                    int c = (y - 1) % n;
                    if (r % 2 > 0) {
                        c = n - 1 - c; // 奇数行从右到左
                    }
                    int row = n - 1 - r;
                    int col = c;
                    int nxt = board[row][col];
                    if (nxt < 0) {
                        nxt = y;
                    }
                    if (!vis[nxt]) {
                        vis[nxt] = true; // 有环的情况下，避免死循环
                        q.add(nxt);
                    }
                }
            }
        }
        return -1; // 无法到达终点
    }

    public int snakesAndLadders2(int[][] board) {
        int n = board.length;
        int[] strange = new int[n * n + 1];
        int index = 1;

        for (int i = n - 1; i >= 0; i--) {
            boolean forward = i % 2 == 0;
            for (int j = 0; j < n; j++) {
                if (forward) {
                    strange[index] = board[i][j];
                } else {
                    strange[index] = board[i][n - 1 - j];
                }
                index++;
            }
        }


        boolean[] vis = new boolean[n * n + 1];
        vis[1] = true;
        List<Integer> q = new ArrayList<>();
        q.add(1);
        for (int step = 0; !q.isEmpty(); step++) {
            List<Integer> tmp = q;
            q = new ArrayList<>();
            for (int x : tmp) {
                if (x == n * n) { // 终点
                    return step;
                }
                for (int y = x + 1; y <= Math.min(x + 6, n * n); y++) {
                    int nxt = strange[y];
                    if (nxt < 0) {
                        nxt = y;
                    }
                    if (!vis[nxt]) {
                        vis[nxt] = true;
                        q.add(nxt);
                    }
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        L_909 l909 = new L_909();
        {
            System.out.println(l909.snakesAndLadders(new int[][]{{-1, -1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1, -1}, {-1, 35, -1, -1, 13, -1}, {-1, -1, -1, -1, -1, -1}, {-1, 15, -1, -1, -1, -1}}));
        }
        {
            System.out.println(l909.snakesAndLadders2(new int[][]{{-1, -1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1, -1}, {-1, 35, -1, -1, 13, -1}, {-1, -1, -1, -1, -1, -1}, {-1, 15, -1, -1, -1, -1}}));
        }
//        {
//            System.out.println(l909.snakesAndLadders(new int[][]{{2, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}}));
//        }
//        {
//            System.out.println(l909.snakesAndLadders2(new int[][]{{2, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}}));
//        }
//        {
//            System.out.println(l909.snakesAndLadders(new int[][]{{-1, -4}, {-1, 3}}));
//        }
//        {
//            System.out.println(l909.snakesAndLadders(new int[][]{{-1, -1}, {-1, 3}}));
//        }
    }
}
