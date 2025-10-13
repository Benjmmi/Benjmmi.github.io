package top100liked;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class L_146 {
    static class LRUCache {
        Map<Integer, int[]> kv = new HashMap<>();
        int capacity;
        int counter = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(new Comparator<int[]>() {

            @Override
            public int compare(int[] o1, int[] o2) {
                // 1 key
                // 2 value
                // 2 time
                return o1[2] - o2[2];
            }
        });

        public LRUCache(int capacity) {
            this.capacity = capacity;
        }

        public int get(int key) {
            if (!kv.containsKey(key)) {
                return -1;
            }
            int[] arr = kv.get(key);
            counter++;
            pq.remove(arr);
            arr[2] = counter;
            pq.offer(arr);
            return arr[1];
        }

        public void put(int key, int value) {
            counter++;
            if (kv.containsKey(key)){
                int[] arr = kv.get(key);
                pq.remove(arr);
            }
            int[] v = new int[]{key, value, counter};
            kv.put(key, v);
            pq.offer(v);
            while (true) {
                if (kv.size() > capacity) {
                    int k = pq.poll()[0];
                    kv.remove(k);
                } else {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        LRUCache lruCache = new LRUCache(2);
        lruCache.put(2, 1);
        lruCache.put(1, 1);
        lruCache.put(2, 3);  // rm 2
        lruCache.put(4, 1);   // rm 1
        System.out.println(lruCache.get(1)); // -1
        System.out.println(lruCache.get(2)); // -1
    }
}
