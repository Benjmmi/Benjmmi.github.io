package top100liked;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class L_73 {
    public void setZeroes(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        Set<Integer> columns = new HashSet<>();

        for (int i = 0; i < matrix.length; i++) {
            boolean hasZero = false;
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 0) {
                    hasZero = true;
                    columns.add(j);
                }
            }
            if (hasZero) {
                Arrays.fill(matrix[i], 0);
            }
        }

        for (Integer column : columns) {
            for (int i = 0; i < m; i++) {
                matrix[i][column] = 0;
            }
        }
    }

    public static void main(String[] args) {

    }
}
