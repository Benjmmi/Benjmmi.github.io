package leetcode;

public class L_909 {
    public int snakesAndLadders(int[][] board) {
        int num = -1;
        int[] strange = new int[board.length * board.length + 1];
        int index = 1;
        for (int i = 0; i < board.length; i++) {
            boolean forward = i / 2 == 0;
            for (int j = 0; j < board.length; j++) {
                if (forward) {
                    strange[index] = board[i][j];
                } else {
                    strange[index] = board[i][board.length - 1 - j];
                }
                index++;
            }
        }

        return num;
    }

    public int bsd(int[] boards, int start, int step) {
        if (start >= boards.length) {
            return 0;
        }
        int max = 6;
        for (int i = start; i < start + 6; i++) {
            max = Math.max(boards[i], max);
        }
        start = max;
        bsd(boards, start, step++);
        return step;
    }

    public static void main(String[] args) {
        L_909 l909 = new L_909();
        {
            System.out.println(l909.snakesAndLadders(new int[][]{{-1, -4}, {-1, 3}}));
        }
        {
            System.out.println(l909.snakesAndLadders(new int[][]{{-1, -1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1, -1}, {-1, 35, -1, -1, 13, -1}, {-1, -1, -1, -1, -1, -1}, {-1, 15, -1, -1, -1, -1}}));
        }
        {
            System.out.println(l909.snakesAndLadders(new int[][]{{-1, -1}, {-1, 3}}));
        }
    }
}
