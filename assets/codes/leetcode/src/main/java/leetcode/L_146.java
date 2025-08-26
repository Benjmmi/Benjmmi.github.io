package leetcode;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class L_146 {
    static class LRUCache {

        public static Integer index = 0;

        class Node implements Comparable<Node> {
            Integer key;
            Integer value;
            Integer lastUsed;


            public Node(Integer key, Integer value) {
                this.key = key;
                this.value = value;
                this.lastUsed = index;
                index++;
            }

            @Override
            public int compareTo(Node o) {
                return this.lastUsed - o.lastUsed;
            }
        }

        Map<Integer, Node> kv = new HashMap<>();
        PriorityQueue<Node> queue;
        private Integer capacity;

        public LRUCache(int capacity) {
            this.capacity = capacity;
            queue = new PriorityQueue<>((o1, o2) -> o1.lastUsed - o2.lastUsed);
        }

        public int get(int key) {
            Node node = kv.get(key);
            if (node == null) {
                return -1;
            }
            node.lastUsed = (index++);
            queue.remove(node);
            queue.offer(node);
            return node.value;
        }

        public void put(int key, int value) {
            Node node = kv.get(key);
            if (node == null) {
                if (queue.size() >= capacity) {
                    Node rm = queue.poll();
                    kv.remove(rm.key);
                }
                node = new Node(key, value);
                kv.put(key, node);
            } else {
                node.value = value;
                node.lastUsed = (index++);
                queue.remove(node);
            }
            queue.offer(node);
        }
    }

    public static void main(String[] args) {
        LRUCache lruCache = new LRUCache(2);
        lruCache.put(1, 1);
        lruCache.put(2, 2);
        System.out.println(lruCache.get(1)); // 1
        lruCache.put(3, 3);    // delete 2
        System.out.println(lruCache.get(2)); // -1
        lruCache.put(4, 4); //   delete 1?
        System.out.println(lruCache.get(1)); // -1
        System.out.println(lruCache.get(3)); // 3
        System.out.println(lruCache.get(4)); // 4


//        PriorityQueue<LRUCache.Node> queue = new PriorityQueue<>();
//        {
//            LRUCache.Node node = new LRUCache.Node(1, 2, 3, 1);
//            queue.offer(node);
//        }
//        {
//            LRUCache.Node node = new LRUCache.Node(3, 2, 1, 1);
//            queue.offer(node);
//        }
//        {
//            LRUCache.Node node = new LRUCache.Node(2, 2, 2, 1);
//            queue.offer(node);
//        }
//        while (true) {
//            LRUCache.Node node = queue.poll();
//            if (node == null) {
//                break;
//            }
//            System.out.println(node.count);
//        }
    }

}
