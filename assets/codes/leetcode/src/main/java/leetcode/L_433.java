package leetcode;

import java.util.*;

public class L_433 {
    public int minMutation(String startGene, String endGene, String[] bank) {
        // 最短路径
        Set<String> cnt = new HashSet<String>();
        Set<String> visited = new HashSet<String>();

        char[] keys = {'A', 'C', 'G', 'T'};
        if (startGene.equals(endGene)) {
            return 0;
        }
        for (String s : bank) {
            cnt.add(s);
        }
        visited.add(startGene);

        if (bank.length == 0) {
            return -1;
        }
        // diff start ++
        Queue<String> queue = new ArrayDeque<>();
        queue.add(startGene);
        for (int step = 1; !queue.isEmpty(); step++) {
            int size = queue.size();
            for (int j = 0; j < size; j++) {
                String curr = queue.poll();
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 4; l++) {
                        assert curr != null;
                        StringBuilder builder = new StringBuilder(curr);
                        builder.setCharAt(k, keys[l]);
                        String next = builder.toString();
                        if (!visited.contains(next) && cnt.contains(next)) {
                            if (next.equals(endGene)) {
                                return step;
                            }
                            queue.offer(next);
                            visited.add(next);
                        }
                    }
                }
            }
        }

        // diff end --

        return -1;
    }


    public static void main(String[] args) {

    }
}
