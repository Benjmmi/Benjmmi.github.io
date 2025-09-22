package leetcode;

public class L_35 {
    public int searchInsert(int[] nums, int target) {
        int start = 0;
        int end = nums.length;
        while ((end - start) > 1) {
            int mid = (start + end) / 2;
            if (nums[mid] < target) {
                start = mid;
            } else {
                end = mid;
            }
        }
        if (nums[start] >= target) {
            return start;
        }
        return end;
    }


    public static void main(String[] args) {
        L_35 l35 = new L_35();
        System.out.println(l35.searchInsert(new int[]{1, 3, 5, 6}, 0)); // 0
        System.out.println(l35.searchInsert(new int[]{1, 3, 5, 6}, 5)); // 2
//        System.out.println(l35.searchInsert(new int[]{1, 3, 5, 6}, 2));
//        System.out.println(l35.searchInsert(new int[]{1, 3, 5, 6}, 7));
//        System.out.println(l35.searchInsert(new int[]{1}, 7));
//        System.out.println(l35.searchInsert(new int[]{1, 8}, 7));
//        System.out.println(l35.searchInsert(new int[]{1, 7}, 7));
    }
}
