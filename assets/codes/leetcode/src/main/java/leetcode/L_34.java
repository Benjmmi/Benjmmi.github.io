package leetcode;

public class L_34 {
    public int[] searchRange(int[] nums, int target) {
        if (nums.length == 0) {
            return new int[]{-1, -1};
        }
        int mid = find(nums, target);
        if (mid == -1) {
            return new int[]{-1, -1};
        }
        int first = findFirst(nums, target, mid);
        int last = findLast(nums, target, mid);
        return new int[]{first, last};
    }

    public int find(int[] nums, int target) {
        int start = -1;
        int end = nums.length-1;
        while (end - start > 1) {
            int mid = (end + start) / 2;
            if (nums[mid] >= target) {
                end = mid;
            } else {
                start = mid;
            }
        }

        return nums[end] == target ? end : -1;
    }

    public int findFirst(int[] nums, int target, int mid) {
        int idx = mid;
        for (int i = mid; i >= 0; i--) {
            if (nums[i] != target) {
                break;
            }
            idx = i;
        }
        return idx;
    }

    public int findLast(int[] nums, int target, int mid) {
        int idx = mid;
        for (int i = mid; i < nums.length; i++) {
            if (nums[i] != target) {
                break;
            }
            idx = i;
        }
        return idx;
    }

    public static void main(String[] args) {
        L_34 l34 = new L_34();
        println(l34.searchRange(new int[]{ 7, 7}, 8));
        println(l34.searchRange(new int[]{5, 7, 7, 8, 8, 10}, 8));
        println(l34.searchRange(new int[]{5, 7, 7, 8, 8, 10}, 6));
        println(l34.searchRange(new int[]{}, 0));
    }

    public static void println(int[] ints) {
        for (int anInt : ints) {
            System.out.print(anInt + ",");
        }
        System.out.println();
    }
}
