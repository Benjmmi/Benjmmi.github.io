package leetcode;

public class L_130 {
    public void solve(char[][] board) {
        {
            int row = 0;
            int low = board.length - 1;
            for (int j = 0; j < board[0].length; j++) {
                if (board[row][j] == 'O') {
                    dfs(board, row, j);
                }
                if (board[low][j] == 'O') {
                    dfs(board, low, j);
                }
            }
        }
        {
            int col = 0;
            int low = board[0].length - 1;
            for (int i = 0; i < board.length; i++) {
                if (board[i][col] == 'O') {
                    dfs(board, i, col);
                }
                if (board[i][low] == 'O') {
                    dfs(board, i, low);
                }
            }
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 'B') {
                    board[i][j] = 'O';
                } else {
                    board[i][j] = 'X';
                }
            }
        }
    }

    public void dfs(char[][] board, int i, int j) {
        if (i < 0 || j < 0 || i >= board.length || j >= board[0].length) {
            return;
        }
        if (board[i][j] == 'X') {
            return;
        }
        if (board[i][j] == 'B') {
            return;
        }
        board[i][j] = 'B';
        dfs(board, i + 1, j);
        dfs(board, i - 1, j);
        dfs(board, i, j + 1);
        dfs(board, i, j - 1);
    }

    public static void main(String[] args) {
        L_130 l130 = new L_130();
    }
}
