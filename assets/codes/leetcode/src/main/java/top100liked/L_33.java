package top100liked;

public class L_33 {
    public int search(int[] nums, int target) {
        if (nums[nums.length - 1] >= target) {
            for (int length = nums.length - 1; length >= 0 && nums[length] >= target; length--) {
                if (nums[length] == target) {
                    return length;
                }
            }
        } else {
            for (int i = 0; i < nums.length && nums[i] <= target; i++) {
                if (nums[i] == target) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {

    }
}
