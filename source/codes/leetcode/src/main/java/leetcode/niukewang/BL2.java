package samle.niukewang;

import java.util.*;

public class BL2 {
    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
        int t = 6; // 总节点数量
        int n = 5; // 当前节点
        int ps = 7; // 边数

        int[][] pairs = new int[][]{
                {1, 0},
                {3, 1},
                {4, 1},
                {5, 3},
                {6, 1},
//                {6, 5},
        };

        int[] parent = new int[7];
        for (int i = 0; i < 7; i++) {
            parent[i] = -1;
        }
        int[] rank = new int[7];
        for (int i = 0; i < 7; i++) {
            rank[i] = 0;
        }

        BL2 bl2 = new BL2();
        for (int i = 0; i < pairs.length; i++) {
            boolean f = bl2.unionF(pairs[i][0], pairs[i][1], parent, rank);
            if (!f) {
                System.out.println("有环");
                System.exit(0);
            }
        }

        System.out.println(rank[5]);
        System.out.println("五环");


    }


    public int findRoot(int num, int[] parent) {
        int root = num;
        while (parent[root] != -1) {
            root = parent[root];
        }
        return root;
    }

    public boolean unionF(int x, int y, int[] parent, int[] rank) {

        int x_root = findRoot(x, parent);
        int y_root = findRoot(y, parent);

        if (x_root == y_root) {
            return false;
        }

        parent[x_root] = y_root;
        if (rank[x_root] == rank[y_root]) {
            rank[x_root] += 1;
        }
        if (rank[x_root] > rank[y_root]) {
            rank[x_root] = rank[y_root] + 1;
        }
        return true;
    }


//
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        int t = scanner.nextInt();
//        int n = scanner.nextInt();
//        int ps = scanner.nextInt();
//        List<Map<Integer, Integer>> pa = new ArrayList<>();
//        for(int i = 0; i < ps; i++) {
//            String[] pair = scanner.next().split(",");
//            Map<Integer, Integer> map = new HashMap<>();
//            map.put(Integer.parseInt(pair[0]), Integer.parseInt(pair[1]));
//            pa.add(map);
//        }
//        System.out.println(new BL2().a(n, pa));
//    }
//
//    Set<Integer> sets = new TreeSet<>();
//
//
//    public int a(int n, List<Map<Integer, Integer>> pa) {
//        List<Integer> integers = new ArrayList<>();
//        Iterator<Map<Integer, Integer>> it = pa.iterator();
//        while (it.hasNext()) {
//            Integer i = it.next().get(n);
//            if (i != null){
//                integers.add(i);
//                sets.add(i);
//                it.remove();
//                a(i,pa);
//            }
//        }
//        return sets.size();
//    }
//
//    public int ab(Map<String,TreeSet<String>> b){
//
//        return 0;
//    }
}
