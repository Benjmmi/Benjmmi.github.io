package top100liked;

import java.util.ArrayList;
import java.util.List;

public class L_54 {

    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> res = new ArrayList<>();
        int[][] guide = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        int m = matrix.length;
        int n = matrix[0].length;
        int i = 0;
        int guideM = 0;
        int row = 0;
        int col = -1;
        while (i < m * n) {
            int[] gg = guide[guideM];
            if (row + gg[0] < m && row + gg[0] >= 0
                    && col + gg[1] < n && col + gg[1] >= 0
                    && matrix[row + gg[0]][col + gg[1]] != -111) {
                res.add(matrix[row + gg[0]][col + gg[1]]);
                matrix[row + gg[0]][col + gg[1]] = -111;
                row += gg[0];
                col += gg[1];
                i++;
            } else {
                guideM++;
                guideM = guideM % guide.length;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        L_54 l54 = new L_54();
        l54.spiralOrder(new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}});
    }
}
