package leetcode;

import java.util.PriorityQueue;

public class L_502 {
    public int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
        // profits.desc,capital.asc
        PriorityQueue<Heap> heaps = new PriorityQueue<>((o1, o2) -> o1.capital - o2.capital);
        // 每次决策前，将启动资金不超过当前总资金的任务加入集合，再在里面取利润最大的任务
        for (int i = 0; i < profits.length; i++) {
            Heap heap = new Heap();
            heap.index = i;
            heap.profit = profits[i];
            heap.capital = capital[i];
            heaps.offer(heap);
        }

        // 不限次数就去除最大致
        int cur = w;
        while (!heaps.isEmpty()) {
            Heap heap = heaps.poll();
            if (heap.capital > cur) {
                continue;
            }
            cur = cur + heap.profit;
        }
        return cur;
    }

    class Heap {
        int index;
        int capital;
        int profit;
    }

    public static void main(String[] args) {
        L_502 l502 = new L_502();
        System.out.println(l502.findMaximizedCapital(2, 0, new int[]{1, 2, 3}, new int[]{0, 1, 1}));
    }
}
