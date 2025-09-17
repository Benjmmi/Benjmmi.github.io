package leetcode;

import java.util.ArrayList;
import java.util.List;

public class L_39 {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> answer = new ArrayList<>();
        dfs(0, target, candidates, answer, new ArrayList<>());
        return answer;
    }

    public void dfs(int i, int left, int[] candidates, List<List<Integer>> ans, List<Integer> path) {
        if (left == 0) {
            // 找到一个合法组合
            ans.add(new ArrayList<>(path));
            return;
        }

        if (i == candidates.length || left < 0) {
            return;
        }

        // 不选
        dfs(i + 1, left, candidates, ans, path);

        // 选
        path.add(candidates[i]);
        dfs(i, left - candidates[i], candidates, ans, path);
        path.remove(path.size() - 1); // 恢复现场
    }


    public static void main(String[] args) {
        L_39 l39 = new L_39();
        System.out.println(l39.combinationSum(new int[]{2, 3, 6, 7}, 7));
    }
}
