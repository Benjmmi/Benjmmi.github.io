package interview_100;

public class L_280 {
    public void wiggleSort(int[] nums) {
        boolean flag = true;
        for (int i = 1; i < nums.length; i++) {
            if (flag) {
                if (nums[i] < nums[i - 1]) {
                    int val = nums[i];
                    nums[i] = nums[i - 1];
                    nums[i - 1] = val;
                }
                flag = false;
            } else {
                if (nums[i] > nums[i - 1]) {
                    int val = nums[i];
                    nums[i] = nums[i - 1];
                    nums[i - 1] = val;
                }
                flag = true;
            }
        }
    }

    public static void main(String[] args) {

    }
}
