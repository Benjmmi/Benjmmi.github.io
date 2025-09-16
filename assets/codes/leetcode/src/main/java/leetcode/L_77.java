package leetcode;

import java.util.*;
import java.util.stream.Collectors;

public class L_77 {
    Set<String> strings = new HashSet<>();

    /**
     *
     * @param n 小于 n
     * @param k 长度 k
     * @return
     */
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> list = new ArrayList<>();
        dfs(n, k, 1, new ArrayList<>(), list);
        return list;
    }

    public void dfs(int n, int k, int i, List<Integer> result, List<List<Integer>> list) {
        if (k < 0 || n - i < k) {
            return;
        }
        if (k == 0) {
            String l = String.join(",", result.stream().map(java.lang.String::valueOf).collect(Collectors.toList()));
            if (strings.add(l)) {
                list.add(result);
            }
            return;
        }
        for (int j = i; j <= n; j++) {
            List<Integer> items = new ArrayList<>();
            items.addAll(result);
            items.add(j);
            dfs(n, k - 1, i + 1, items, list);
            dfs(n, k, i + 1, new ArrayList<>(), list);
        }
    }

    public static void main(String[] args) {
        L_77 l77 = new L_77();
        System.out.println(l77.combine(4, 2));
    }
}
