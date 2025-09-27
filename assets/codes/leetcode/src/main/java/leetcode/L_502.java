package leetcode;

import java.util.PriorityQueue;

public class L_502 {
    public int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
        // profits.desc,capital.asc
        // 存放当前
        PriorityQueue<Heap> heaps = new PriorityQueue<>((o1, o2) -> o2.profit - o1.profit);
        // 存放以后
        PriorityQueue<Heap> future = new PriorityQueue<>((o1, o2) -> o1.capital - o2.capital);

        // 每次决策前，将启动资金不超过当前总资金的任务加入集合，再在里面取利润最大的任务
        for (int i = 0; i < profits.length; i++) {
            Heap heap = new Heap();
            heap.index = i;
            heap.profit = profits[i];
            heap.capital = capital[i];
            if (capital[i] > w) {
                future.offer(heap);
            } else {
                heaps.offer(heap);
            }
        }
        int cur = w;
        for (int i = 0; i < k; i++) {
            while (!future.isEmpty() && future.peek().capital <= cur) {
                heaps.offer(future.poll());
            }
            if (heaps.isEmpty()) {
                break;
            }
            cur += heaps.poll().profit;
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
        System.out.println(l502.findMaximizedCapital(2, 2, new int[]{1, 2, 3}, new int[]{0, 1, 2}));
        System.out.println(l502.findMaximizedCapital(2, 0, new int[]{1, 2, 3}, new int[]{0, 1, 1}));
    }
}
