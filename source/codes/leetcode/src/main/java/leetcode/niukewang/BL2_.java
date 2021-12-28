package samle.niukewang;

public class BL2_ {
    public static void main(String[] args) {
        int[][] pairs = new int[][]{
                {1, 0},
                {3, 1},
                {4, 1},
                {5, 3},
                {6, 1},
                {6, 5},
        };

        int ix = 5;
        // 节点数量
        int[] parents = new int[pairs.length + 1];
        int[] size = new int[pairs.length + 1];
        // 当没有连线时，自己就是根
        for (int i = 0; i < parents.length; i++) {
            parents[i] = i;
            size[i] = 1;
        }

        int ans = 0, b = 0;
        BL2_ bl21 = new BL2_();
        for (int i = 0; i < pairs.length; i++) {
            // 本身就有关系
            if (pairs[i][0] == ix || pairs[i][1] == ix) {
                b++;
            }
            // 更新他们的根节点
            bl21.union(pairs[i][0], pairs[i][1], parents, size);
        }

        for (int i = 0; i < pairs.length; i++) {
            // 判断是否属于同一个根节点，有就记一个
            if (bl21.conn(pairs[i][0], pairs[i][1], parents)) {
                ans++;
            }
        }

        System.out.println(ans - b - 1);
    }

    public void union(int x, int y, int[] parents, int[] size) {
        int x_root = find(x, parents);
        int y_root = find(y, parents);
        if (x_root == y_root) {
            return;
        }

        if (size[x_root] > size[y_root]) {
            parents[y_root] = x_root;
            size[x_root] += size[y_root];
        } else {
            parents[x_root] = y_root;
            size[y_root] += size[x_root];
        }
    }

    public boolean conn(int x, int y, int[] parents) {
        int x_root = find(x, parents);
        int y_root = find(y, parents);
        return x_root == y_root;
    }

    public int find(int x, int[] parents) {
        while (parents[x] != x) {
            x = parents[x];
        }
        return x;
    }
}
