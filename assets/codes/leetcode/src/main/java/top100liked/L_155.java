package top100liked;

import java.util.*;

public class L_155 {

}

class MinStack {
    //    List<Integer> list = new ArrayList<>();
//    TreeMap<Integer, Integer> treeMap = new TreeMap<>();
//
    public MinStack() {

    }

    //
//    public void push(int val) {
//        list.add(val);
//        treeMap.put(val, treeMap.getOrDefault(val, 0) + 1);
//    }
//
//    public void pop() {
//        int e = list.remove(list.size() - 1);
//        if (treeMap.containsKey(e)) {
//            if (treeMap.get(e) - 1 == 0) {
//                treeMap.remove(e);
//            } else {
//                treeMap.put(e, treeMap.get(e) - 1);
//            }
//        }
//    }
//
//    public int top() {
//        return list.get(list.size() - 1);
//    }
//
//    public int getMin() {
//        return treeMap.firstKey();
//    }
    Stack<Integer> list = new Stack<>();
    Stack<Integer> minS = new Stack<>();
    int min = Integer.MAX_VALUE;

    //
    public void push(int val) {
        list.push(val);
        min = Math.min(min, val);
        minS.push(min);
    }

    public void pop() {
        list.pop();
        minS.pop();
        if (minS.isEmpty()) {
            min = Integer.MAX_VALUE;
        } else {
            min = minS.peek();
        }
    }

    public int top() {
        return list.peek();
    }

    public int getMin() {
        return minS.peek();
    }
}
