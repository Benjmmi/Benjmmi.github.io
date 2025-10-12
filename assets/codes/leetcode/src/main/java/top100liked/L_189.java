package top100liked;

public class L_189 {
    public void rotate(int[] nums, int k) {
        k %= nums.length;
        if (k == 0) {
            return;
        }
        int[] newNums = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            newNums[(i + k) % nums.length] = nums[i];
        }
        for (int i = 0; i < nums.length; i++) {
            nums[i] = newNums[i];
        }

    }

    public static void main(String[] args) {
        L_189 l189 = new L_189();
        {
            int[] nums = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
            l189.rotate(nums, 6);
            println(nums);
        }
        {
            int[] nums = new int[]{3};
            l189.rotate(nums, 1);
            println(nums);
        }
    }

    private static void println(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + ",");
        }
        System.out.println();
    }
}
