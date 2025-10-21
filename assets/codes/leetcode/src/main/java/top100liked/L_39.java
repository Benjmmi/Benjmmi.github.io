package top100liked;

import java.util.ArrayList;
import java.util.List;

public class L_39 {

    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> res = new ArrayList<>();

        dfs(candidates, target, new ArrayList<>(), res, 0);
        return res;
    }

    public void dfs(int[] candidates, int target, List<Integer> item, List<List<Integer>> res, int start) {
        if (target == 0) {
            res.add(new ArrayList<>(item));
            return;
        }
        if (target < 0) {
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            List<Integer> newItem = new ArrayList<>(item);
            newItem.add(candidates[i]);
            dfs(candidates, target - candidates[i], newItem, res, Math.max(start, i));
        }
    }

    public static void main(String[] args) {

    }
}
