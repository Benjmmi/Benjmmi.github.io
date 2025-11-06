package top100liked;

public class L_152 {

    public int maxProduct(int[] nums) {
        int[] maxProduct = new int[nums.length + 1];
        int[] minProduct = new int[nums.length + 1];
        int max = nums[0];
        maxProduct[0] = nums[0];

        for (int i = 1; i < nums.length; i++) {
            maxProduct[i] = Math.max(nums[i], Math.max(maxProduct[i - 1] * nums[i], minProduct[i - 1] * nums[i]));
            minProduct[i] = Math.min(nums[i], Math.min(maxProduct[i - 1] * nums[i], minProduct[i - 1] * nums[i]));
            max = Math.max(max, Math.max(maxProduct[i], minProduct[i]));
        }
        return max;
    }

    public static void main(String[] args) {
        L_152 l152 = new L_152();
        System.out.println(l152.maxProduct(new int[]{2, 3, -2, 4}));
        System.out.println(l152.maxProduct(new int[]{-2, 0, -1}));
        System.out.println(l152.maxProduct(new int[]{-2, -3, 1, 2}));
        System.out.println(l152.maxProduct(new int[]{-2, 3, -4, -5, -6}));
    }
}
