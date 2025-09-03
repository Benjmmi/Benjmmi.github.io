package leetcode;

import java.util.*;

public class L_207 {


    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<Integer>[] g = new ArrayList[numCourses];
        Arrays.setAll(g, i -> new ArrayList<>());

        int[] color = new int[numCourses];
        for (int[] prerequisite : prerequisites) {
            g[prerequisite[1]].add(prerequisite[0]);
        }

        for (int i = 0; i < color.length; i++) {
            if (color[i] == 0 && dfs(g, color, i)) {
                return false;
            }
        }


        return true;
    }

    public boolean dfs(List<Integer>[] g, int[] colors, int x) {
        colors[x] = 1;
        for (Integer y : g[x]) {
            if (colors[y] == 1 || colors[y] == 0 && dfs(g, colors, y)) {
                return true; // 找到了环
            }
        }
        colors[x] = 2;

        return false;
    }

    public boolean canFinish2(int numCourses, int[][] prerequisites) {
        boolean finish = true;
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] prerequisite : prerequisites) {
            if (prerequisite[1] == prerequisite[0]) {
                return false;
            }
            if (dfs2(map, prerequisite[0], prerequisite[1])) {
                return false;
            }
            List<Integer> list = map.getOrDefault(prerequisite[1], new ArrayList<>());
            list.add(prerequisite[0]);
            map.put(prerequisite[1], list);
        }
        return finish;
    }

    public boolean dfs2(Map<Integer, List<Integer>> map, int key, int target) {
        if (map.get(key) == null) {
            return false;
        }
        if (map.get(key).contains(target)) {
            return true;
        }
        List<Integer> integers = map.get(key);
        for (Integer integer : integers) {
            if (dfs2(map, integer, target)) {
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) {
        L_207 l207 = new L_207();
        {
            l207.canFinish(3, new int[][]{{0, 2}, {1, 2}, {2, 0}});
        }
    }
}
