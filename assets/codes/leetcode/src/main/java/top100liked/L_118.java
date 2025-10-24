package top100liked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class L_118 {

    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> res = new ArrayList<>();
        if (numRows == 0) return res;
        res.add(Arrays.asList(1));
        if (numRows == 1) return res;
        res.add(Arrays.asList(1, 1));
        if (numRows == 2) return res;

        for (int i = 3; i <= numRows; i++) {
            List<Integer> list = new ArrayList<>();
            list.add(1);
            List<Integer> preList = res.get(i - 2);
            for (int j = 1; j < i-1; j++) {
                list.add(preList.get(j - 1) + preList.get(j));
            }
            list.add(1);
            res.add(list);
        }
        return res;
    }

    public static void main(String[] args) {
        L_118 l118 = new L_118();
        System.out.println(l118.generate(5));
    }
}
