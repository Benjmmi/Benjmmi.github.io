package top100liked;

import java.util.Stack;

// TODO 多练
public class L_84 {
    public int largestRectangleArea(int[] heights) {
        int max = 0;
        Stack<Integer> stack = new Stack<>();

        int i = 0;
        for (; i < heights.length; i++) {
            if (stack.isEmpty() || heights[i] > heights[stack.peek()]) {
                stack.push(i);
                continue;
            }
            while (!stack.isEmpty() && heights[stack.peek()] > heights[i]) {
                int topIndex = stack.pop();
                int new_top_index = stack.isEmpty() ? -1 : stack.peek();
                int height = heights[topIndex];
                int width = i - new_top_index - 1;
                max = Math.max(max, width * height);
            }
            stack.push(i);
        }

        while (!stack.isEmpty()) {
            int topIndex = stack.pop();
            int new_top_index = stack.isEmpty() ? -1 : stack.peek();
            int height = heights[topIndex];
            int width = i - new_top_index - 1;
            max = Math.max(max, width * height);
        }

        return max;
    }

//    public int largestRectangleArea(int[] heights) {
//        int max = 0;
//        for (int i = 0; i < heights.length; i++) {
//            int j = i;
//            for (; j < heights.length && heights[j] >= heights[i]; j++) {
//
//            }
//            int k = i;
//            for (; k >= 0 && heights[k] >= heights[i]; k--) {
//
//            }
//            max = Math.max(max, (j - k - 1) * heights[i]);
//        }
//        return max;
//    }

    public static void main(String[] args) {
        L_84 l84 = new L_84();
//        System.out.println(l84.largestRectangleArea(new int[]{2, 1, 5, 6, 2, 3}));
        System.out.println(l84.largestRectangleArea(new int[]{2, 4}));
    }
}
