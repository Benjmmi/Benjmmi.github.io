package top100liked;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class L_56 {
    public int[][] merge(int[][] intervals) {
        PriorityQueue<int[]> queue = new PriorityQueue<>(new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] == o2[0] ? o2[1] - o1[1] : o1[0] - o2[0];
            }
        });
        for (int[] interval : intervals) {
            queue.add(interval);
        }

        List<int[]> res = new ArrayList<>();
        int i = 0;
        int min = queue.peek()[0];
        int max = queue.peek()[1];
        while (!queue.isEmpty()) {
            int[] interval = queue.poll();
            if (interval[0] >= min && interval[0] <= max) {
                min = Math.min(min, interval[0]);
                max = Math.max(max, interval[1]);
            } else if (interval[1] >= min && interval[1] <= max) {
                min = Math.min(min, interval[0]);
                max = Math.max(max, interval[1]);
            } else {
                res.add(new int[]{min, max});
                min = interval[0];
                max = interval[1];
                i++;
            }
        }
        res.add(new int[]{min, max});
        int[][] resut = new int[res.size()][2];
        res.toArray(resut);
        return resut;
    }

    public static void main(String[] args) {
        L_56 l = new L_56();
        println(l.merge(new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}}));
    }

    private static void println(int[][] merge) {
        for (int i = 0; i < merge.length; i++) {
            System.out.print("[" + merge[i][0] + "," + merge[i][1] + "],");
        }
        System.out.println();
    }
}
