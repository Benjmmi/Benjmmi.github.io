package leetcode;

import java.util.*;
import java.util.stream.Collectors;

public class L_77 {


    /**
     *
     * @param n 小于 n
     * @param k 长度 k
     * @return
     */
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> list = new ArrayList<>();
        dfs(n, k, new ArrayList<>(), list);
        return list;
    }

    public void dfs(int n, int k, List<Integer> result, List<List<Integer>> list) {
        int len = k - result.size();
        if (len == 0) {
            list.add(new ArrayList<>(result));
            return;
        }

        for (int i = n; i >= len; i--) {
            result.add(i);
            dfs(i - 1, k, result, list);
            result.remove(result.size() - 1);
        }
    }

    public static void main(String[] args) {
        L_77 l77 = new L_77();
        System.out.println(l77.combine(4, 2));
//        for (int i = 0; i < 10; i++) {
//            for (int j = i + 1; j < 10; j++) {
//                for (int k = j + 1; k < 10; k++) {
//                    System.out.println(i + "," + j + "," + k);
//                }
//            }
//        }
    }
}
