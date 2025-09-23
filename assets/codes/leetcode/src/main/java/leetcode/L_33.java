package leetcode;

public class L_33 {
    public int search(int[] nums, int target) {
        int minIndex = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] < nums[minIndex]) {
                minIndex = i;
            }
        }

        if (target <= nums[nums.length - 1]) {
            return findMid(nums, target, minIndex-1, nums.length);
        }

        return findMid(nums, target, -1, minIndex);
    }

    public int findMid(int[] nums, int target, int start, int end) {
        while (start + 1 < end) {
            int mid = (start + end) / 2;
            if (nums[mid] >= target) {
                end = mid;
            } else {
                start = mid;
            }
        }
        return nums[end] == target ? end : -1;
    }

    public static void main(String[] args) {
        L_33 l33 = new L_33();
        System.out.println(l33.search(new int[]{4, 5, 6, 7, 0, 1, 2}, 0));
        System.out.println(l33.search(new int[]{4, 5, 6, 7, 0, 1, 2}, 2));
        System.out.println(l33.search(new int[]{4, 5, 6, 7, 0, 1, 2}, 4));
        System.out.println(l33.search(new int[]{4, 5, 6, 7, 0, 1, 2}, 8));
        System.out.println(l33.search(new int[]{2}, 0));
        System.out.println(l33.search(new int[]{2}, 2));
    }
}
