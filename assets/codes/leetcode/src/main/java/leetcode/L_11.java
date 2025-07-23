package leetcode;

public class L_11 {
    public int maxArea(int[] height) {
        int low = 0;
        int high = height.length - 1;
        int max = 0;
        while (low < high) {
            int min = Math.min(height[low], height[high]);
            max = Math.max(max, min * (high - low));

            if (height[low] < height[high]) {
                low++;
            } else {
                high--;
            }
        }
        return max;
    }

    public static void main(String[] args) {
        L_11 l11 = new L_11();
        {
            System.out.println(l11.maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}));
        }
        {
            System.out.println(l11.maxArea(new int[]{1, 1}));
        }
    }
}
