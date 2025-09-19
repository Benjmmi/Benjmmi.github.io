package leetcode;

public class L_79 {
    int[][] location = new int[][]{
            {0, 1},
            {0, -1},
            {1, 0},
            {-1, 0}
    };

    public boolean exist(char[][] board, String word) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                char c = board[i][j];
                board[i][j] = '.';
                if (c == word.charAt(0) && dsf(board, word, i, j, 1)) {
                    return true;
                }
                board[i][j] = c;
            }
        }
        return false;
    }

    private boolean dsf(char[][] board, String word, int row, int col, int index) {
        if (index == word.length()) {
            return true;
        }
        for (int[] ints : location) {
            int i = row + ints[0];
            int j = col + ints[1];
            if (i < 0 || i >= board.length || j < 0 || j >= board[0].length || board[i][j] == '.') {
                continue;
            }
            if (board[i][j] == word.charAt(index)) {
                char c = board[i][j];
                board[i][j] = '.';
                if (dsf(board, word, i, j, index + 1)) {
                    return true;
                } else {
                    board[i][j] = c;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        L_79 l79 = new L_79();
        System.out.println(l79.exist(new char[][]{{'A', 'B', 'C', 'E'}, {'S', 'F', 'C', 'S'}, {'A', 'D', 'E', 'E'}}, "ABCCED"));
        System.out.println(l79.exist(new char[][]{{'A', 'B', 'C', 'E'}, {'S', 'F', 'C', 'S'}, {'A', 'D', 'E', 'E'}}, "SEE"));
        System.out.println(l79.exist(new char[][]{{'A', 'B', 'C', 'E'}, {'S', 'F', 'C', 'S'}, {'A', 'D', 'E', 'E'}}, "ABCB"));
    }
}
