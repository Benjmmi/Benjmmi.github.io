package leetcode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L_207 {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        boolean finish = true;
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int[] prerequisite : prerequisites) {
            if (prerequisite[1] == prerequisite[0]) {
                return false;
            }
            if (dfs(map, prerequisite[0], prerequisite[1])) {
                return false;
            }
            if (!map.containsKey(prerequisite[1])){

            }
//            map.put(prerequisite[1], prerequisite[0]);
        }
        return finish;
    }

    public boolean dfs(Map<Integer, List<Integer>> map, int key, int target) {
        if (map.get(key) == null) {
            return false;
        }
        if (map.get(key) == target) {
            return true;
        }
        return dfs(map, map.get(key), target);
    }

    public static void main(String[] args) {
        L_207 l207 = new L_207();
        {
            l207.canFinish(3, new int[][]{{0, 2}, {1, 2}, {2, 0}});
        }
    }
}
