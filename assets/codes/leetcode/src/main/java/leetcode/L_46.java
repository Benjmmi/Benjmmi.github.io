package leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class L_46 {


    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> answer = new ArrayList<>();
        dsf(0, nums, new ArrayList<>(Arrays.asList(new Integer[nums.length])), answer, new boolean[nums.length]);
        return answer;
    }

    public void dsf(int i, int[] nums, List<Integer> path, List<List<Integer>> answer, boolean[] onPath) {

        if (i == nums.length) {
            answer.add(new ArrayList<>(path));
            return;
        }
        for (int j = 0; j < nums.length; j++) {
            if (!onPath[j]) {
                path.set(i, nums[j]);
                onPath[j] = true;
                dsf(i + 1, nums, path, answer, onPath);
                onPath[j] = false;
            }
        }
    }

    public static void main(String[] args) {

    }
}
