package leetcode;

public class L_74 {
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length;
        int n = matrix[0].length;

        int start = 0;
        int end = m * n - 1;

        if (matrix[0][0] == target) {
            return true;
        }
        if (matrix[m - 1][n - 1] == target) {
            return true;
        }

        while (end - start > 1) {
            int mid = (end + start) / 2;
            int row = mid / n;
            int col = mid % n;
            if (matrix[row][col] == target) {
                return true;
            } else if (matrix[row][col] > target) {
                end = mid;
            } else {
                start = mid;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        L_74 l74 = new L_74();
        System.out.println(l74.searchMatrix(new int[][]{{1, 3}, {10, 11}}, 1)); // true
        System.out.println(l74.searchMatrix(new int[][]{{1, 3}, {10, 11}}, 3)); // true
        System.out.println(l74.searchMatrix(new int[][]{{1, 3}, {10, 11}}, 10)); // true
        System.out.println(l74.searchMatrix(new int[][]{{1, 3}, {10, 11}}, 11)); // true
        System.out.println(l74.searchMatrix(new int[][]{{1, 3}, {10, 11}}, -1)); // false
        System.out.println(l74.searchMatrix(new int[][]{{1, 3}, {10, 11}}, 111)); // false
//        System.out.println(l74.searchMatrix(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 1)); // true
//        System.out.println(l74.searchMatrix(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 3)); // true
//        System.out.println(l74.searchMatrix(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 13));
//        System.out.println(l74.searchMatrix(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 20)); // true
//        System.out.println(l74.searchMatrix(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 34)); // true
//        System.out.println(l74.searchMatrix(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 60)); // true
    }
}
