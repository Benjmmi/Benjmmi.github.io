package top100liked;

import javax.sound.sampled.Line;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class L_207 {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // 0 代表 白
        // 1 代表 灰色
        // 2 代表黑色
        Set<Integer>[] dependence = new HashSet[numCourses];


        for (int i = 0; i < numCourses; i++) {
            dependence[i] = new HashSet<>();
        }

        for (int i = 0; i < prerequisites.length; i++) {
            dependence[prerequisites[i][1]].add(prerequisites[i][0]);
        }

        int[] visited = new int[numCourses];
        for (int i = 0; i < numCourses; i++) {
            if (!dfs(i, dependence, visited)){
                return false;
            }
        }

//        for (int[] prerequisite : prerequisites) {
//            int from = prerequisite[1];
//            int to = prerequisite[0];
//            if (dependence[to].contains(from)) {
//                return false;
//            }
//            dependence[from].addAll(dependence[to]);
//
//            if (dependence[to].contains(from)) {
//                return false;
//            }
//        }


        return true;
    }

    public boolean dfs(int from, Set<Integer>[] dependence, int[] visited) {
        if (visited[from] == 1) return false;
        if (visited[from] == 2) return true;
        visited[from] = 1;
        for (Integer i : dependence[from]) {
            if (!dfs(i, dependence, visited)) return false;
        }
        visited[from] = 2;

        return true;
    }


//    public boolean dfs(int from, Set<Integer>[] dependence, List<Integer> visit, int[] visited) {
//        if (dependence[from].isEmpty()) {
//            return true;
//        }
//        if (visit.contains(from)) {
//            return false;
//        }
//        if (visited[from] == 1) {
//            return true;
//        }
//        List<Integer> list = new ArrayList<>(visit);
//        list.add(from);
//        Set<Integer> sets = dependence[from];
//        for (Integer set : sets) {
//            visited[set] = 1;
//            if (!dfs(set, dependence, list, visited)) {
//                return false;
//            }
//        }
//
//        return true;
//    }

    public static void main(String[] args) {
        L_207 l207 = new L_207();
        System.out.println(l207.canFinish(4, new int[][]{{0, 1}, {2, 3}, {1, 2}, {3, 0}}));
        System.out.println(l207.canFinish(2, new int[][]{{1, 0}}));
        System.out.println(l207.canFinish(2, new int[][]{{1, 0}, {0, 1}}));
        System.out.println(l207.canFinish(3, new int[][]{{0, 2}, {1, 2}, {2, 0}}));
        System.out.println(l207.canFinish(20, new int[][]{
                {0, 10},
                {3, 18},
                {5, 5},
                {6, 11},
                {11, 14},
                {13, 1},
                {15, 1},
                {17, 4},
        }));
    }
}
