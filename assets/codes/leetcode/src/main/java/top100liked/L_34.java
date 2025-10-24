package top100liked;

public class L_34 {
    public int[] searchRange(int[] nums, int target) {
        int low = -1, high = -1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target && low == -1) {
                low = i;

            }
            if (nums[i] == target) {
                high = i;
            }
            if (nums[i] > target) {
                break;
            }
        }
        return new int[]{low, high};
    }

    public static void main(String[] args) {

    }
}
