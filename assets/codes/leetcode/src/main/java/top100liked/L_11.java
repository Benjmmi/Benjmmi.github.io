package top100liked;

public class L_11 {
    // TODO Stupid
    public int maxArea(int[] height) {
        int max = 0;
//        for (int i = 0; i < height.length; i++) {
//            for (int j = i+1; j < height.length; j++) {
//                int min = Math.min(height[i], height[j]);
//                max = Math.max(max, min * (j - i));
//            }
//        }
        int left = 0;
        int right = height.length - 1;
        while (left < right) {
            max = Math.max(max, Math.min(height[left], height[right]) * (right - left));
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return max;
    }

    public static void main(String[] args) {
        L_11 l11 = new L_11();
        System.out.println(l11.maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}));
        System.out.println(l11.maxArea(new int[]{1, 1}));
        System.out.println(l11.maxArea(new int[]{1, 2, 1}));
        System.out.println(l11.maxArea(new int[]{2, 2, 2, 2, 3, 2, 2, 2}));
    }
}
