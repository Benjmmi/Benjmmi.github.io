package leetcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class L_155 {
    static class MinStack {
        List<Integer> list;
        List<Integer> minList;
        Integer min = Integer.MAX_VALUE;

        public MinStack() {
            list = new ArrayList<>();
            minList = new ArrayList<>();
        }

        public void push(int val) {
            list.add(val);
            minList.add(Math.min(min, val));
        }

        public void pop() {
            list.remove(list.size() - 1);
            minList.remove(list.size() - 1);
        }

        public int top() {
            return list.get(list.size() - 1);
        }

        public int getMin() {
            return minList.get(list.size() - 1);
        }
    }


    public static void main(String[] args) {
        MinStack obj = new MinStack();
        obj.push(1);
        obj.pop();
        int param_3 = obj.top();
        int param_4 = obj.getMin();
    }
}
