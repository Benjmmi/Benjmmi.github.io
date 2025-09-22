package leetcode;

public class L_162 {
    public int findPeakElement(int[] nums) {
        int left = -1;
        int right = nums.length - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[mid + 1]) {
                right = mid;
            } else {
                left = mid;
            }
        }
        return right;
    }


    public static void main(String[] args) {
        L_162 l162 = new L_162();
        System.out.println(l162.findPeakElement(new int[]{1, 3, 2, 1}));
        System.out.println(l162.findPeakElement(new int[]{1, 2, 3, 1}));
        System.out.println(l162.findPeakElement(new int[]{1, 2, 1, 3, 5, 6, 4}));
    }
}
