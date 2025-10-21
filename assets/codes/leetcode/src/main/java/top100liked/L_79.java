package top100liked;

import java.util.ArrayList;
import java.util.List;

public class L_79 {

    int[][] location = new int[][]{
            {0, 1},
            {0, -1},
            {1, 0},
            {-1, 0}
    };

    public boolean exist(char[][] board, String word) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == word.charAt(0)) {
                    board[i][j] = '.';
                    if (exist(board, word, 0, i, j)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    public boolean exist(char[][] board, String word, int index, int x, int y) {
        if (index == word.length()) return true;

        for (int[] ints : location) {
            int row = ints[0] + x, col = ints[1] + y;
            if (row < 0 || row >= board.length || col < 0 || col >= board[0].length) continue;
            if (board[x][y] == word.charAt(index)) {
                char c = word.charAt(index);
                board[row][col] = '.';
                if (exist(board, word, index + 1, row, col)) {
                    return true;
                }
                board[row][col] = c;
            }
        }
        return false;
    }

    public static void main(String[] args) {

    }


}
