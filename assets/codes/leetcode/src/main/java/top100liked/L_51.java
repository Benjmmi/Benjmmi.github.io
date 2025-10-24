package top100liked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class L_51 {
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        // 左上
        boolean[] dag1 = new boolean[n * 2];
        // 右上
        boolean[] dag2 = new boolean[n * 2];
        boolean[] col = new boolean[n];

        dfs(n, new int[n], dag1, dag2, col, 0, result);

        return result;
    }

    public void dfs(int n, int[] queens, boolean[] dag1, boolean[] dag2, boolean[] col, int row, List<List<String>> result) {
        if (row == n) {
            List<String> list = new ArrayList<>();
            for (int i : queens) {
                char[] chars = new char[n];
                Arrays.fill(chars, '.');
                chars[i] = 'Q';
                list.add(new String(chars));
            }
            result.add(list);
            return;
        }

        for (int c = 0; c < n; c++) {
            int ru = row + c;
            int lu = row - c + n - 1;
            if (!col[c] && !dag1[lu] && !dag2[ru]) {
                queens[row] = c;
                col[c] = dag2[ru] = dag1[lu] = true;
                dfs(n, queens, dag1, dag2, col, row + 1, result);
                col[c] = dag2[ru] = dag1[lu] = false;
            }

        }

    }

//
//    public void backtrack(List<List<String>> result, char[] chars, List<String> board, int row, int n) {
//        if (n == row) {
//            result.add(new ArrayList<>(board));
//            return;
//        }
//
//        for (int col = 0; col < n; col++) {
//            Arrays.fill(chars, '.');
//            if (!validCol(board, col)) continue;
//            if (!validLU(board, row - 1, col - 1)) continue;
//            if (!validRU(board, row - 1, col + 1, n)) continue;
//            chars[col] = 'Q';
//            board.add(new String(chars));
//            backtrack(result, chars, board, row + 1, n);
//            board.remove(board.size() - 1);
//        }
//    }
//
//    public boolean validCol(List<String> board, int col) {
//        for (int i = 0; i < board.size(); i++) {
//            if (board.get(i).charAt(col) == 'Q') {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public boolean validLU(List<String> board, int row, int col) {
//        if (board.isEmpty()) return true;
//        while (row >= 0 && col >= 0) {
//            if (board.get(row).charAt(col) == 'Q') {
//                return false;
//            }
//            row--;
//            col--;
//        }
//        return true;
//    }
//
//    public boolean validRU(List<String> board, int row, int col, int n) {
//        if (board.isEmpty()) return true;
//        while (row >= 0 && col < n) {
//            if (board.get(row).charAt(col) == 'Q') {
//                return false;
//            }
//            row--;
//            col++;
//        }
//
//        return true;
//    }


    public static void main(String[] args) {
        L_51 l51 = new L_51();
        System.out.println(l51.solveNQueens(5));
    }
}
