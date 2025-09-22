package leetcode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class L_427 {

    class Node {
        public boolean val;
        public boolean isLeaf;
        public Node topLeft;
        public Node topRight;
        public Node bottomLeft;
        public Node bottomRight;


        public Node() {
            this.val = false;
            this.isLeaf = false;
            this.topLeft = null;
            this.topRight = null;
            this.bottomLeft = null;
            this.bottomRight = null;
        }

        public Node(boolean val, boolean isLeaf) {
            this.val = val;
            this.isLeaf = isLeaf;
            this.topLeft = null;
            this.topRight = null;
            this.bottomLeft = null;
            this.bottomRight = null;
        }

        public Node(boolean val, boolean isLeaf, Node topLeft, Node topRight, Node bottomLeft, Node bottomRight) {
            this.val = val;
            this.isLeaf = isLeaf;
            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomLeft = bottomLeft;
            this.bottomRight = bottomRight;
        }
    }

    public Node construct(int[][] grid) {
        return dfs(grid, 0, 0, grid.length, grid.length);
    }


    public Node dfs(int[][] grid, int top, int left, int bottom, int right) {
        boolean same = true;
        for (int i = top; i < bottom; ++i) {
            for (int j = left; j < right; ++j) {
                if (grid[i][j] != grid[top][left]) {
                    same = false;
                    break;
                }
            }
            if (!same) {
                break;
            }
        }

        if (same) {
            return new Node(grid[top][left] == 1, true);
        }

        Node ret = new Node(
                true,
                false,
                dfs(grid, top, left, (top + bottom) / 2, (left + right) / 2),
                dfs(grid, top, (left + right) / 2, (top + bottom) / 2, right),
                dfs(grid, (top + bottom) / 2, left, bottom, (left + right) / 2),
                dfs(grid, (top + bottom) / 2, (left + right) / 2, bottom, right)
        );
        return ret;
    }


    public static void main(String[] args) {

    }

}
