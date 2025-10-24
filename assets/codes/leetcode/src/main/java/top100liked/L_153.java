package top100liked;

public class L_153 {

    public int findMin(int[] nums) {
        if (nums.length == 1) return nums[0];
        int min = nums[0];
        if (nums[nums.length - 1] < nums[0]) {
            for (int i = nums.length - 1; i >= 0 && nums[i] <= nums[nums.length - 1]; i--) {
                if (nums[i] < min) {
                    min = nums[i];
                }
            }
        } else {
            return nums[0];
        }

        return min;
    }

    public static void main(String[] args) {

    }
}
