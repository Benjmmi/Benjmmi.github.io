package top100liked;

public class L_42 {
    public int trap(int[] height) {
        // 找到最后一个最大值的位置，然后从右往左
        int highest = 0;

        for (int i = 0; i < height.length; i++) {
            if (height[i] >= height[highest]) {
                highest = i;
            }
        }

        int start = 0;
        int cur = 0;
        for (int i = 1; i <= highest; i++) {
            if (height[i] > height[start]) {
                start = i;
                continue;
            }
            cur += height[start] - height[i];
        }

        start = height.length - 1;
        for (int i = height.length - 2; i >= highest; i--) {

            if (height[i] > height[start]) {
                start = i;
                continue;
            }
            cur += height[start] - height[i];
        }

        return cur;
    }

    public static void main(String[] args) {
        L_42 l42 = new L_42();
        System.out.println(l42.trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}));
        System.out.println(l42.trap(new int[]{4, 2, 0, 3, 2, 5}));
    }
}
