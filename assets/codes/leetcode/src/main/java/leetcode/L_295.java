package leetcode;

import java.util.*;

public class L_295 {

    static class MedianFinder {
        // 降序 1,2,3,4....10 first
        PriorityQueue<Integer> leftHeap = new PriorityQueue<>((a, b) -> b - a);
        double avg = 0; // (10 + 11) /2
        // 升序 first  11,12,13,14,15....16
        PriorityQueue<Integer> rightHeap = new PriorityQueue<>();

        public MedianFinder() {

        }

        public void addNum(int num) {
            if (leftHeap.isEmpty()) {
                leftHeap.add(num);
                avg = num;
                return;
            }
            // 如果 num > 中位数 放在右边
            if (num > avg) {
                rightHeap.add(num);
            } else if (num <= avg) {
                // 如果 num <= 中位数 放在左边
                leftHeap.add(num);
            }

            int leftSize = leftHeap.size();
            int rightSize = rightHeap.size();
            if ((leftSize + rightSize) % 2 == 0) {
                // 保持两边平衡
                while (leftHeap.size() != rightHeap.size()) {
                    if (leftHeap.size() > rightHeap.size()) {
                        rightHeap.add(leftHeap.poll());
                    } else {
                        leftHeap.add(rightHeap.poll());
                    }
                }
                avg = (leftHeap.peek() + rightHeap.peek()) / 2.0;
            } else {
                // 保持左边大于右边
                while (leftHeap.size() < rightHeap.size()) {
                    leftHeap.add(rightHeap.poll());
                }
                // 取出左边为中间值
                avg = leftHeap.peek();
            }
        }

        public double findMedian() {
            return avg;
        }
    }

    public static void main(String[] args) {
//        {
//            MedianFinder c = new MedianFinder();
//            c.addNum(1);
//            c.addNum(2);
//            System.out.println(c.findMedian());
//            c.addNum(3);
//            System.out.println(c.findMedian());
//        }
        {
            MedianFinder c = new MedianFinder();
            c.addNum(6);
            System.out.println(c.findMedian()); // 6.00000
            c.addNum(10);
            System.out.println(c.findMedian()); // 8.0
            c.addNum(2);
            System.out.println(c.findMedian()); // 6.0
            c.addNum(6);
            System.out.println(c.findMedian()); // 6.0
            c.addNum(5);
            System.out.println(c.findMedian());  // 5.5
            c.addNum(0);
            System.out.println(c.findMedian()); // 6.0
            c.addNum(6);
            System.out.println(c.findMedian()); // 5.5
            c.addNum(3);
            System.out.println(c.findMedian()); // 5.0
            c.addNum(1);
            System.out.println(c.findMedian()); // 4.0
            c.addNum(0);
            System.out.println(c.findMedian()); // 3.0
            c.addNum(0);
            System.out.println(c.findMedian()); //
        }
    }
}
