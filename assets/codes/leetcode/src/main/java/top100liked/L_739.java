package top100liked;

import java.util.Stack;

public class L_739 {
    public int[] dailyTemperatures(int[] temperatures) {
        Stack<Integer> stack = new Stack<>();
        int[] result = new int[temperatures.length];
        for (int i = 0; i < temperatures.length; i++) {
            if (stack.isEmpty()) {
                stack.push(i);
                continue;
            }
            while (!stack.isEmpty() && temperatures[stack.peek()] < temperatures[i]) {
                int index = stack.pop();
                result[index] = i - index;
            }
            stack.push(i);
        }
        return result;
    }

    public static void main(String[] args) {
        L_739 l739 = new L_739();
        l739.dailyTemperatures(new int[]{73, 74, 71, 69, 72, 76, 73});
        l739.dailyTemperatures(new int[]{30, 40, 50, 60});
        l739.dailyTemperatures(new int[]{30, 60, 90});
    }
}
