package top100liked;

import java.util.ArrayList;
import java.util.List;

public class L_46 {


    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        dfs(nums, res, new ArrayList<>(), new int[nums.length], 0);
        return res;
    }

    public void dfs(int[] nums, List<List<Integer>> res, List<Integer> item, int[] mark, int start) {
        if (start == nums.length) {
            res.add(item);
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (mark[i] == 0) {
                mark[i] = 1;
                List<Integer> newItem = new ArrayList<>(item);
                newItem.add(nums[i]);
                dfs(nums, res, newItem, mark, start + 1);
                mark[i] = 0;
            }
        }
    }

    public static void main(String[] args) {

    }
}
