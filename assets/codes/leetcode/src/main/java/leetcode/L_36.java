package leetcode;

import java.util.HashSet;
import java.util.Set;

public class L_36 {
    public boolean isValidSudoku(char[][] board) {
        Set<Character> col = new HashSet<>();
        Set<Character> row = new HashSet<>();
        Set<Character> reduce = new HashSet<>();
        for (int i = 0; i < board.length; i++) {
            col.clear();
            row.clear();
            for (int j = 0; j < board[0].length; j++) {
                if (board[j][i] != '.' && !col.add(board[j][i])) {
                    return false;
                }
                if (board[i][j] != '.' && !row.add(board[i][j])) {
                    return false;
                }
                //
                //   11   14   17
                //
                //   41   44  47
                //
                //   71   74  77
                //
                if (
                        (i == 1 && j == 1)
                                ||
                                (i == 1 && j == 4)
                                ||
                                (i == 1 && j == 7)
                                ||
                                (i == 4 && j == 1)
                                ||
                                (i == 4 && j == 4)
                                ||
                                (i == 4 && j == 7)
                                ||
                                (i == 7 && j == 1)
                                ||
                                (i == 7 && j == 4)
                                ||
                                (i == 7 && j == 7)
                ) {
                    reduce.clear();
                    if (board[i][j] != '.' && !reduce.add(board[i][j])) {
                        return false;
                    }
                    if (board[i - 1][j] != '.' && !reduce.add(board[i - 1][j])) {
                        return false;
                    }
                    if (board[i][j - 1] != '.' && !reduce.add(board[i][j - 1])) {
                        return false;
                    }
                    if (board[i - 1][j - 1] != '.' && !reduce.add(board[i - 1][j - 1])) {
                        return false;
                    }

                    if (board[i + 1][j] != '.' && !reduce.add(board[i + 1][j])) {
                        return false;
                    }
                    if (board[i][j + 1] != '.' && !reduce.add(board[i][j + 1])) {
                        return false;
                    }
                    if (board[i + 1][j + 1] != '.' && !reduce.add(board[i + 1][j + 1])) {
                        return false;
                    }
                    if (board[i - 1][j + 1] != '.' && !reduce.add(board[i - 1][j + 1])) {
                        return false;
                    }
                    if (board[i + 1][j - 1] != '.' && !reduce.add(board[i + 1][j - 1])) {
                        return false;
                    }
                }

            }
        }
        return true;
    }

    public static void main(String[] args) {
        L_36 l36 = new L_36();
        System.out.println(l36.isValidSudoku(new char[][]{{'5','3','.','.','7','.','.','.','.'},{'6','.','.','1','9','5','.','.','.'},{'.','9','8','.','.','.','.','6','.'},{'8','.','.','.','6','.','.','.','3'},{'4','.','.','8','.','3','.','.','1'},{'7','.','.','.','2','.','.','.','6'},{'.','6','.','.','.','.','2','8','.'},{'.','.','.','4','1','9','.','.','5'},{'.','.','.','.','8','.','.','7','9'}}));
        System.out.println(l36.isValidSudoku(new char[][]{{'8','3','.','.','7','.','.','.','.'},{'6','.','.','1','9','5','.','.','.'},{'.','9','8','.','.','.','.','6','.'},{'8','.','.','.','6','.','.','.','3'},{'4','.','.','8','.','3','.','.','1'},{'7','.','.','.','2','.','.','.','6'},{'.','6','.','.','.','.','2','8','.'},{'.','.','.','4','1','9','.','.','5'},{'.','.','.','.','8','.','.','7','9'}}));
    }
}
