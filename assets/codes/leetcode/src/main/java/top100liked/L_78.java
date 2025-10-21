package top100liked;

import java.util.ArrayList;
import java.util.List;

public class L_78 {

    public List<List<Integer>> subsets(int[] nums) {

        List<List<Integer>> res = new ArrayList<>();

        dfs(nums,res,new ArrayList<>(),new int[nums.length], 0);

        return res;
    }


    public void dfs(int[] nums, List<List<Integer>> res, List<Integer> item, int[] mark, int start) {
        if (start > nums.length) {
            return;
        }
        res.add(item);
        for (int i = start; i < nums.length; i++) {
            if (mark[i] == 0) {
                mark[i] = 1;
                List<Integer> newItem = new ArrayList<>(item);
                newItem.add(nums[i]);
                dfs(nums, res, newItem, mark, Math.max(start + 1, i));
                mark[i] = 0;
            }
        }
    }

    public static void main(String[] args) {
        L_78 l78 = new L_78();
        List<List<Integer>> res = l78.subsets(new int[]{1, 2, 3});
    }
}
