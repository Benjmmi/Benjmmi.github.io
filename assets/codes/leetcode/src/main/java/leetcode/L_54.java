package leetcode;

import java.util.ArrayList;
import java.util.List;

public class L_54 {
    public List<Integer> spiralOrder(int[][] matrix) {
        // border = 101
        // 0 = col++
        // 1 = row++
        // 2 = col--
        // 3 = row--
        int length = matrix.length * matrix[0].length;
        int row = 0;
        int col = 0;
        int guide = 0;
        int step = 0;
        List<Integer> list = new ArrayList<>();
        list.add(matrix[0][0]);
        matrix[0][0] = 101;
        while (true) {
            if (guide == 0) {
                col++;
            } else if (guide == 1) {
                row++;
            } else if (guide == 2) {
                col--;
            } else if (guide == 3) {
                row--;
            }

            if (col < 0 || row < 0 || col >= matrix[0].length || row >= matrix.length || matrix[row][col] == 101) {
                if (guide == 0) {
                    col--;
                    row++;
                } else if (guide == 1) {
                    row--;
                    col--;
                } else if (guide == 2) {
                    col++;
                    row--;
                } else if (guide == 3) {
                    row++;
                    col++;
                }
                guide++;
                guide %= 4;
            }

            if (col < 0 || row < 0 || col >= matrix[0].length || row >= matrix.length || matrix[row][col] == 101) {
                break;
            }

            list.add(matrix[row][col]);
            matrix[row][col] = 101;

        }

        return list;
    }

    public static void main(String[] args) {
        L_54 l54 = new L_54();
        System.out.println(l54.spiralOrder(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}));
        System.out.println(l54.spiralOrder(new int[][]{{1, 2}, {3, 4}}));
        System.out.println(l54.spiralOrder(new int[][]{{1}}));
        System.out.println(l54.spiralOrder(new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}}));
    }
}
