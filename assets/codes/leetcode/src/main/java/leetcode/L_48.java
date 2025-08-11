package leetcode;

public class L_48 {
    public void rotate(int[][] matrix) {
        // 1. 对角线 反转
        int rl = matrix.length - 1;


        // 正对角线反转
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[rl - i - 1][rl - i];
            }
        }

        // 上下翻转
        for (int i = 0; i < matrix.length / 2; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                int or = rl - i;
                int temp = matrix[i][j];
                matrix[i][j] = matrix[or][j];
                matrix[or][j] = temp;
            }
        }

        //  反对角线反转
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix[0].length; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

    }

    public static void main(String[] args) {
        L_48 l48 = new L_48();
        {
            int[][] ii = new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
            l48.rotate(ii);
            println(ii);
        }
        {
            int[][] ii = new int[][]{{5, 1, 9, 11}, {2, 4, 8, 10}, {13, 3, 6, 7}, {15, 14, 12, 16}};
            l48.rotate(ii);
            println(ii);
        }
    }

    public static void println(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + ",\t");
            }
            System.out.println();
        }
        System.out.println();
    }
}
