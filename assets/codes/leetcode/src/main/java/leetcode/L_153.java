package leetcode;

public class L_153 {
    public int findMin(int[] nums) {

        int last = nums[nums.length - 1];

        int left = -1;
        int right = nums.length-1;
        // 找到最大值，分界点+1 就行
        while (right - left > 1) {
            int mid = (right + left) / 2;
            if (nums[mid] < last) {
                // 在右边
                right = mid;
            } else {
                // 在左边
                left = mid;
            }
        }

        return nums[right];
    }


    public static void main(String[] args) {
        L_153 l153 = new L_153();
        System.out.println(l153.findMin(new int[]{2}));
        System.out.println(l153.findMin(new int[]{2, 1}));
        System.out.println(l153.findMin(new int[]{1, 2}));
        System.out.println(l153.findMin(new int[]{3, 1, 2}));
        System.out.println(l153.findMin(new int[]{3, 4, 5, 1, 2}));
        System.out.println(l153.findMin(new int[]{7, 8, 9, 2, 3}));
        System.out.println(l153.findMin(new int[]{4, 5, 6, 7, 0, 1, 2}));
        System.out.println(l153.findMin(new int[]{11, 13, 15, 17}));
    }
}
